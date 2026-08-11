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
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.io.OutputStream;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
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

    // Producto
    private String criterioProducto;
    private Producto productoSeleccionado;
    private int cantidadLinea;
    private double precioUnitarioLinea;

    // Cliente
    private String criterioCliente;
    private Cliente clienteSeleccionado;
    private String clienteNoRegistradoNombre;

    // CAI
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

        // Valores iniciales
        this.cantidadLinea = 1;
        this.descuento = 0;
        this.impuesto = 15;
        this.subtotal = 0;
        this.total = 0;
        this.precioUnitarioLinea = 0;

        cargarCAIActivo();
    }

    private void cargarCAIActivo() {

        try {

            this.caiActivo = caiService.obtenerCAIActivo();

            if (caiActivo != null) {

                this.numeroFactura =
                        caiService.obtenerSiguienteNumeroFactura();

            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_WARN,
                                "CAI",
                                "No hay CAI activo"
                        )
                );
            }

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "CAI",
                            "No se pudo cargar el CAI activo"
                    )
            );
        }
    }

    @Override
    public void cargarFacturas() {

        this.listaFacturas =
                facturaInteractor.listarTodas();
    }

    @Override
    public void crearFactura() {

        this.detalleActual = new ArrayList<>();

        this.clienteSeleccionado = null;
        this.clienteNoRegistradoNombre = null;

        this.productoSeleccionado = null;
        this.criterioProducto = null;

        this.cantidadLinea = 1;
        this.precioUnitarioLinea = 0;

        this.descuento = 0;

        // Impuesto fijo del 15%
        this.impuesto = 15;

        this.subtotal = 0;
        this.total = 0;

        cargarCAIActivo();

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Información",
                        "Nueva factura iniciada"
                )
        );
    }

    // ==============================
    // PRODUCTOS
    // ==============================

    public List<Producto> completarProductos(String query) {
        String criterio = query == null ? "" : query.trim();
        if (criterio.isEmpty()) {
            return productoService.listarTodos();
        }
        return productoService.buscarProductos(criterio);
    }

    public void onProductoSelect() {

        if (productoSeleccionado != null) {

            this.precioUnitarioLinea =
                    productoSeleccionado.getPrecioVenta();
        } else {

            this.precioUnitarioLinea = 0;
        }
    }

    public void agregarLinea() {

        if (productoSeleccionado == null) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Advertencia",
                            "Seleccione un producto"
                    )
            );

            return;
        }

        if (cantidadLinea <= 0) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Cantidad",
                            "La cantidad debe ser mayor que cero"
                    )
            );

            return;
        }

        if (precioUnitarioLinea <= 0) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Precio",
                            "El precio unitario no es válido"
                    )
            );

            return;
        }

        boolean stockDisponible =
                productoService.verificarStock(
                        productoSeleccionado.getIdProducto(),
                        cantidadLinea
                );

        if (!stockDisponible) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Stock",
                            "No hay stock suficiente para el producto seleccionado"
                    )
            );

            return;
        }

        DetalleFactura detalle = new DetalleFactura();

        detalle.setIdProducto(
                productoSeleccionado.getIdProducto()
        );

        detalle.setCantidad(cantidadLinea);

        detalle.setPrecioUnitario(
                precioUnitarioLinea
        );

        detalle.setSubtotal(
                cantidadLinea * precioUnitarioLinea
        );

        detalleActual.add(detalle);

        recalcularSubtotal();

        // Limpiar producto
        this.productoSeleccionado = null;
        this.criterioProducto = null;
        this.cantidadLinea = 1;
        this.precioUnitarioLinea = 0;
    }

    public void eliminarLinea(DetalleFactura detalle) {

        if (detalle != null) {

            detalleActual.remove(detalle);

            recalcularSubtotal();
        }
    }

    // ==============================
    // CLIENTES
    // ==============================

    public List<Cliente> completarClientes(String query) {
        String criterio = query == null ? "" : query.trim();
        if (criterio.isEmpty()) {
            return clienteService.listarTodos();
        }
        return clienteService.buscarClientes(criterio);
    }

    // ==============================
    // CÁLCULOS
    // ==============================

    private void recalcularSubtotal() {

        double suma = 0;

        for (DetalleFactura detalle : detalleActual) {

            suma += detalle.getSubtotal();
        }

        this.subtotal = suma;

        this.total =
                calcularTotal(
                        subtotal,
                        descuento,
                        impuesto
                );
    }

    public void onValoresFinancierosChange() {

        this.total =
                calcularTotal(
                        subtotal,
                        descuento,
                        impuesto
                );
    }

    private double calcularTotal(
            double subtotal,
            double descuento,
            double impuesto) {

        /*
         * descuento = porcentaje
         * impuesto  = porcentaje
         */

        double montoDescuento =
                subtotal * (descuento / 100);

        double subtotalConDescuento =
                subtotal - montoDescuento;

        double montoImpuesto =
                subtotalConDescuento * (impuesto / 100);

        return subtotalConDescuento + montoImpuesto;
    }

    // ==============================
    // GUARDAR FACTURA
    // ==============================

    @Override
    public void guardarFactura() {

        try {

            if (detalleActual == null ||
                    detalleActual.isEmpty()) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_WARN,
                                "Advertencia",
                                "Debe agregar al menos un producto a la factura"
                        )
                );

                return;
            }

            if (caiActivo == null) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "CAI",
                                "No hay un CAI activo"
                        )
                );

                return;
            }

            if (!caiService.validarRangoFactura(
                    numeroFactura,
                    caiActivo)) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "CAI",
                                "El número de factura está fuera del rango autorizado"
                        )
                );

                return;
            }

            Factura factura = new Factura();

            factura.setNumeroFactura(numeroFactura);

            factura.setCai(
                    caiActivo.getCai()
            );

            factura.setFechaFactura(
                    LocalDateTime.now()
            );

            if (clienteSeleccionado != null) {

                factura.setIdCliente(
                        clienteSeleccionado.getIdCliente()
                );

            } else {

                // Cliente no registrado
                factura.setIdCliente(0);
            }

            factura.setSubtotal(subtotal);

            factura.setDescuento(descuento);

            factura.setImpuesto(impuesto);

            factura.setTotal(
                    calcularTotal(
                            subtotal,
                            descuento,
                            impuesto
                    )
            );

            factura.setEstado("activo");

            facturaInteractor.crearFactura(
                    factura,
                    detalleActual
            );

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Éxito",
                            "Factura guardada correctamente"
                    )
            );

            cargarFacturas();

            // Preparar una nueva factura
            this.detalleActual = new ArrayList<>();

            this.clienteSeleccionado = null;
            this.clienteNoRegistradoNombre = null;

            this.productoSeleccionado = null;
            this.criterioProducto = null;

            this.cantidadLinea = 1;
            this.precioUnitarioLinea = 0;

            this.subtotal = 0;
            this.descuento = 0;
            this.impuesto = 15;
            this.total = 0;

            cargarCAIActivo();

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            e.getMessage()
                    )
            );
        }
    }

    // ==============================
    // DETALLES
    // ==============================

    @Override
    public void obtenerDetalles() {

        if (facturaSeleccionada != null) {

            this.detalleActual =
                    detalleService.obtenerDetallesPorFactura(
                            facturaSeleccionada.getIdFactura()
                    );
        }
    }

    // ==============================
    // PDF
    // ==============================

    @Override
    public void generarPDF() {

        if (facturaSeleccionada == null) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Factura",
                            "Seleccione una factura"
                    )
            );

            return;
        }

        try {

            byte[] pdf = facturaInteractor.generarPDF(
                    facturaSeleccionada.getIdFactura()
            );

            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            ec.responseReset();
            ec.setResponseContentType("application/pdf");
            ec.setResponseContentLength(pdf.length);
            ec.setResponseHeader(
                    "Content-Disposition",
                    "attachment; filename=\"factura_" + facturaSeleccionada.getNumeroFactura() + ".pdf\""
            );

            try (OutputStream out = ec.getResponseOutputStream()) {
                out.write(pdf);
            }

            fc.responseComplete();

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "No se pudo generar el PDF: " + e.getMessage()
                    )
            );
        }
    }

    @Override
    public void generarReporte() {
        // Pendiente
    }

    @Override
    public void limpiarFormulario() {

        numeroFactura = 0;

        subtotal = 0;
        descuento = 0;
        impuesto = 15;
        total = 0;

        cantidadLinea = 1;
        precioUnitarioLinea = 0;

        productoSeleccionado = null;
        clienteSeleccionado = null;

        clienteNoRegistradoNombre = null;

        detalleActual = new ArrayList<>();

        cargarCAIActivo();
    }

    // ==============================
    // GETTERS Y SETTERS
    // ==============================

    @Override
    public Factura getFacturaSeleccionada() {
        return facturaSeleccionada;
    }

    public void setFacturaSeleccionada(
            Factura facturaSeleccionada) {

        this.facturaSeleccionada =
                facturaSeleccionada;
    }

    @Override
    public List<Factura> getListaFacturas() {
        return listaFacturas;
    }

    @Override
    public List<DetalleFactura> getDetalleActual() {
        return detalleActual;
    }

    @Override
    public int getNumeroFactura() {
        return numeroFactura;
    }

    @Override
    public void setNumeroFactura(int numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    @Override
    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public double getDescuento() {
        return descuento;
    }

    @Override
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    @Override
    public double getImpuesto() {
        return impuesto;
    }

    @Override
    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    @Override
    public double getTotal() {
        return total;
    }

    @Override
    public void setTotal(double total) {
        this.total = total;
    }

    public String getCriterioProducto() {
        return criterioProducto;
    }

    public void setCriterioProducto(
            String criterioProducto) {

        this.criterioProducto = criterioProducto;
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(
            Producto productoSeleccionado) {

        this.productoSeleccionado =
                productoSeleccionado;
    }

    public int getCantidadLinea() {
        return cantidadLinea;
    }

    public void setCantidadLinea(int cantidadLinea) {
        this.cantidadLinea = cantidadLinea;
    }

    public double getPrecioUnitarioLinea() {
        return precioUnitarioLinea;
    }

    public void setPrecioUnitarioLinea(
            double precioUnitarioLinea) {

        this.precioUnitarioLinea =
                precioUnitarioLinea;
    }

    public String getCriterioCliente() {
        return criterioCliente;
    }

    public void setCriterioCliente(
            String criterioCliente) {

        this.criterioCliente = criterioCliente;
    }

    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(
            Cliente clienteSeleccionado) {

        this.clienteSeleccionado =
                clienteSeleccionado;
    }

    public String getClienteNoRegistradoNombre() {
        return clienteNoRegistradoNombre;
    }

    public void setClienteNoRegistradoNombre(
            String clienteNoRegistradoNombre) {

        this.clienteNoRegistradoNombre =
                clienteNoRegistradoNombre;
    }

    public CAI getCaiActivo() {
        return caiActivo;
    }

    public ClienteService getClienteService() {
        return clienteService;
    }
}