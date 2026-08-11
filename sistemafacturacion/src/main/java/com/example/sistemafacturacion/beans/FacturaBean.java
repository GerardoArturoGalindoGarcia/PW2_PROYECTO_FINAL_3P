package com.example.sistemafacturacion.beans;

import com.example.sistemafacturacion.data.CAI;
import com.example.sistemafacturacion.data.Cliente;
import com.example.sistemafacturacion.data.DetalleFactura;
import com.example.sistemafacturacion.data.Producto;
import com.example.sistemafacturacion.data.Factura;
import com.example.sistemafacturacion.interfaces.interactor.FacturaInteractor;
import com.example.sistemafacturacion.interfaces.viewmodel.FacturaViewModel;
import com.example.sistemafacturacion.services.CAIService;
import com.example.sistemafacturacion.services.ClienteService;
import com.example.sistemafacturacion.services.DetalleFacturaService;
import com.example.sistemafacturacion.services.FacturaService;
import com.example.sistemafacturacion.services.ProductoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("facturaBean")
@ViewScoped
public class FacturaBean implements FacturaViewModel, Serializable {
    private final FacturaInteractor facturaInteractor;
    private final CAIService caiService;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final DetalleFacturaService detalleService;

    private List<Factura> listaFacturas;
    private List<DetalleFactura> detalleActual;
    private Factura facturaSeleccionada;
    private int numeroFactura;
    private double subtotal;
    private double descuento;
    private double impuesto;
    private double total;

    // Campos para UI: búsqueda/autocomplete
    private String criterioProducto;
    private Producto productoSeleccionado;
    private int cantidadLinea;
    private double precioUnitarioLinea;

    private String criterioCliente;
    private Cliente clienteSeleccionado;
    private String clienteNoRegistradoNombre; // nombre libre si no existe cliente

    private CAI caiActivo;

    public FacturaBean() {
        this.facturaInteractor = new FacturaService();
        this.caiService = new CAIService();
        this.productoService = new ProductoService();
        this.clienteService = new ClienteService();
        this.detalleService = new DetalleFacturaService();
        this.detalleActual = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        cargarFacturas();
        cargarCAIActivo();
    }

    private void cargarCAIActivo() {
        try {
            this.caiActivo = caiService.obtenerCAIActivo();
            if (caiActivo != null) {
                // Auto completar número de factura
                this.numeroFactura = caiService.obtenerSiguienteNumeroFactura();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "CAI", "No hay CAI activo"));
        }
    }

    @Override
    public void cargarFacturas() {
        this.listaFacturas = facturaInteractor.listarTodas();
    }

    @Override
    public void crearFactura() {
        // Reiniciar formulario y recargar CAI
        this.detalleActual = new ArrayList<>();
        this.clienteSeleccionado = null;
        this.clienteNoRegistradoNombre = null;
        this.productoSeleccionado = null;
        this.criterioProducto = null;
        this.cantidadLinea = 1;
        this.precioUnitarioLinea = 0;
        this.descuento = 0;
        this.impuesto = 0;
        this.subtotal = 0;
        this.total = 0;
        cargarCAIActivo();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Información", "Nueva factura iniciada"));
    }

    // Autocomplete productos por criterio
    public List<Producto> completarProductos(String query) {
        return productoService.buscarProductos(query);
    }

    // Autocomplete clientes por criterio
    public List<Cliente> completarClientes(String query) {
        return clienteService.buscarClientes(query);
    }

    // Al seleccionar producto, rellenar precio unitario
    public void onProductoSelect() {
        if (productoSeleccionado != null) {
            this.precioUnitarioLinea = productoSeleccionado.getPrecioVenta();
        }
    }

    // Agregar línea al detalle
    public void agregarLinea() {
        if (productoSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un producto"));
            return;
        }
        if (!productoService.verificarStock(productoSeleccionado.getIdProducto(), cantidadLinea)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock", "No hay stock suficiente"));
            return;
        }
        DetalleFactura d = new DetalleFactura();
        d.setIdProducto(productoSeleccionado.getIdProducto());
        d.setCantidad(cantidadLinea);
        d.setPrecioUnitario(precioUnitarioLinea);
        d.setSubtotal(cantidadLinea * precioUnitarioLinea);
        detalleActual.add(d);
        recalcularSubtotal();
        // limpiar campos de línea
        this.productoSeleccionado = null;
        this.criterioProducto = null;
        this.cantidadLinea = 1;
        this.precioUnitarioLinea = 0;
    }

    public void eliminarLinea(DetalleFactura detalle) {
        detalleActual.remove(detalle);
        recalcularSubtotal();
    }

    private void recalcularSubtotal() {
        double s = 0;
        for (DetalleFactura d : detalleActual) s += d.getSubtotal();
        this.subtotal = s;
        this.total = facturaInteractor.calcularTotal(subtotal, descuento, impuesto);
    }

    // Cuando cambian descuento o impuesto desde UI
    public void onValoresFinancierosChange() {
        this.total = facturaInteractor.calcularTotal(subtotal, descuento, impuesto);
    }

    @Override
    public void guardarFactura() {
        try {
            if (detalleActual.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe agregar detalles a la factura"));
                return;
            }
            if (caiActivo == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "CAI", "No hay CAI activo"));
                return;
            }
            if (!caiService.validarRangoFactura(numeroFactura, caiActivo)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "CAI", "Número de factura fuera de rango del CAI"));
                return;
            }

            Factura factura = new Factura();
            factura.setNumeroFactura(numeroFactura);
            factura.setCai(caiActivo.getCai());
            factura.setFechaFactura(LocalDateTime.now());
            // Si cliente seleccionado asignar id, si no usar 0 (no registrado)
            factura.setIdCliente(clienteSeleccionado != null ? clienteSeleccionado.getIdCliente() : 0);
            factura.setSubtotal(subtotal);
            factura.setDescuento(descuento);
            factura.setImpuesto(impuesto);
            factura.setTotal(facturaInteractor.calcularTotal(subtotal, descuento, impuesto));
            factura.setEstado("activo");

            // Llamar al servicio que orquesta la transacción
            Factura creada = facturaInteractor.crearFactura(factura, detalleActual);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Factura guardada correctamente"));
            cargarFacturas();
            crearFactura(); // reiniciar formulario
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    @Override
    public void obtenerDetalles() {
        if (facturaSeleccionada != null) {
            // Cargar detalles de la factura seleccionada usando el servicio
            this.detalleActual = detalleService.obtenerDetallesPorFactura(facturaSeleccionada.getIdFactura());
        }
    }

    @Override
    public void generarPDF() {
        if (facturaSeleccionada != null) {
            try {
                byte[] pdf = facturaInteractor.generarPDF(facturaSeleccionada.getIdFactura());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Éxito", "PDF generado correctamente"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }
    }

    @Override
    public void generarReporte() {

    }

    @Override
    public void limpiarFormulario() {
        numeroFactura = 0;
        subtotal = 0;
        descuento = 0;
        impuesto = 0;
        total = 0;
        detalleActual = new ArrayList<>();
    }

    // Getters y Setters
    @Override
    public Factura getFacturaSeleccionada() { return facturaSeleccionada; }
    public void setFacturaSeleccionada(Factura facturaSeleccionada) { this.facturaSeleccionada = facturaSeleccionada; }
    @Override
    public List<Factura> getListaFacturas() { return listaFacturas; }
    @Override
    public List<DetalleFactura> getDetalleActual() { return detalleActual; }
    @Override
    public int getNumeroFactura() { return numeroFactura; }
    @Override
    public void setNumeroFactura(int numeroFactura) { this.numeroFactura = numeroFactura; }
    @Override
    public double getSubtotal() { return subtotal; }
    @Override
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    @Override
    public double getDescuento() { return descuento; }
    @Override
    public void setDescuento(double descuento) { this.descuento = descuento; }
    @Override
    public double getImpuesto() { return impuesto; }
    @Override
    public void setImpuesto(double impuesto) { this.impuesto = impuesto; }
    @Override
    public double getTotal() { return total; }
    @Override
    public void setTotal(double total) { this.total = total; }

    public String getCriterioProducto() { return criterioProducto; }
    public void setCriterioProducto(String criterioProducto) { this.criterioProducto = criterioProducto; }
    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public void setProductoSeleccionado(Producto productoSeleccionado) { this.productoSeleccionado = productoSeleccionado; }
    public int getCantidadLinea() { return cantidadLinea; }
    public void setCantidadLinea(int cantidadLinea) { this.cantidadLinea = cantidadLinea; }
    public double getPrecioUnitarioLinea() { return precioUnitarioLinea; }
    public void setPrecioUnitarioLinea(double precioUnitarioLinea) { this.precioUnitarioLinea = precioUnitarioLinea; }

    public String getCriterioCliente() { return criterioCliente; }
    public void setCriterioCliente(String criterioCliente) { this.criterioCliente = criterioCliente; }
    public Cliente getClienteSeleccionado() { return clienteSeleccionado; }
    public void setClienteSeleccionado(Cliente clienteSeleccionado) { this.clienteSeleccionado = clienteSeleccionado; }
    public String getClienteNoRegistradoNombre() { return clienteNoRegistradoNombre; }
    public void setClienteNoRegistradoNombre(String clienteNoRegistradoNombre) { this.clienteNoRegistradoNombre = clienteNoRegistradoNombre; }

    public CAI getCaiActivo() { return caiActivo; }

    // Exponer el servicio de clientes para usar en EL (autocomplete)
    public ClienteService getClienteService() { return clienteService; }

    private double calcularTotal(double subtotal, double descuento, double impuesto) {
        return facturaInteractor.calcularTotal(subtotal, descuento, impuesto);
    }
}
