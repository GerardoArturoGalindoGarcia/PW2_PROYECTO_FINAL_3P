package com.example.sistemafacturacion.interfaces.interactor;

import com.example.sistemafacturacion.data.Factura;
import com.example.sistemafacturacion.data.DetalleFactura;
import java.time.LocalDateTime;
import java.util.List;

public interface FacturaInteractor {
    Factura crearFactura(Factura factura, List<DetalleFactura> detalles);
    Factura obtenerPorId(int idFactura);
    List<Factura> listarTodas();
    boolean eliminarFactura(int idFactura);
    List<Factura> obtenerPorCliente(int idCliente);
    List<Factura> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    byte[] generarPDF(int idFactura);
    double calcularTotal(double subtotal, double descuento, double impuesto);
}
