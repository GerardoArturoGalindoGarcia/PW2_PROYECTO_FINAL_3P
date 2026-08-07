package com.example.sistemafacturacion.beans;

import com.example.sistemafacturacion.data.Factura;
import com.example.sistemafacturacion.data.DetalleFactura;
import com.example.sistemafacturacion.interfaces.interactor.FacturaInteractor;
import com.example.sistemafacturacion.interfaces.viewmodel.FacturaViewModel;
import com.example.sistemafacturacion.services.FacturaService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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
    private List<Factura> listaFacturas;
    private List<DetalleFactura> detalleActual;
    private Factura facturaSeleccionada;
    private int numeroFactura;
    private double subtotal;
    private double descuento;
    private double impuesto;
    private double total;

    public FacturaBean() {
        this.facturaInteractor = new FacturaService();
        this.detalleActual = new ArrayList<>();
        cargarFacturas();
    }

    @Override
    public void cargarFacturas() {
        this.listaFacturas = facturaInteractor.listarTodas();
    }

    @Override
    public void crearFactura() {
        try {
            detalleActual = new ArrayList<>();
            limpiarFormulario();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Información", "Nueva factura iniciada"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    @Override
    public void guardarFactura() {
        try {
            if (detalleActual.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", 
                        "Debe agregar detalles a la factura"));
                return;
            }
            
            Factura factura = new Factura();
            factura.setNumeroFactura(numeroFactura);
            factura.setFechaFactura(LocalDateTime.now());
            factura.setSubtotal(subtotal);
            factura.setDescuento(descuento);
            factura.setImpuesto(impuesto);
            factura.setTotal(calcularTotal(subtotal, descuento, impuesto));
            factura.setEstado("activo");
            
            facturaInteractor.crearFactura(factura, detalleActual);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Éxito", "Factura guardada correctamente"));
            cargarFacturas();
            limpiarFormulario();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    @Override
    public void obtenerDetalles() {
        if (facturaSeleccionada != null) {
            
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
    
    private double calcularTotal(double subtotal, double descuento, double impuesto) {
        return facturaInteractor.calcularTotal(subtotal, descuento, impuesto);
    }
}
