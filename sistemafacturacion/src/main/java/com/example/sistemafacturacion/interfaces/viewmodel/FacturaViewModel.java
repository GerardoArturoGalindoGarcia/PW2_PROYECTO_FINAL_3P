package com.example.sistemafacturacion.interfaces.viewmodel;

import com.example.sistemafacturacion.data.Factura;
import com.example.sistemafacturacion.data.DetalleFactura;
import java.time.LocalDateTime;
import java.util.List;

public interface FacturaViewModel {
    void cargarFacturas();
    void crearFactura();
    void guardarFactura();
    void obtenerDetalles();
    void generarPDF();
    void generarReporte();
    void limpiarFormulario();
    Factura getFacturaSeleccionada();
    List<Factura> getListaFacturas();
    List<DetalleFactura> getDetalleActual();
    int getNumeroFactura();
    void setNumeroFactura(int numeroFactura);
    double getSubtotal();
    void setSubtotal(double subtotal);
    double getDescuento();
    void setDescuento(double descuento);
    double getImpuesto();
    void setImpuesto(double impuesto);
    double getTotal();
    void setTotal(double total);
}
