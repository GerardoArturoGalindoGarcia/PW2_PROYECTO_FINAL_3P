package com.example.sistemafacturacion.services;

import com.example.sistemafacturacion.data.Factura;
import com.example.sistemafacturacion.data.DetalleFactura;
import com.example.sistemafacturacion.database.FacturaRepositoryImpl;
import com.example.sistemafacturacion.interfaces.interactor.FacturaInteractor;
import com.example.sistemafacturacion.interfaces.repository.FacturaRepository;
import java.time.LocalDateTime;
import java.util.List;

public class FacturaService implements FacturaInteractor {
    private final FacturaRepository facturaRepository;

    public FacturaService() {
        this.facturaRepository = new FacturaRepositoryImpl();
    }

    @Override
    public Factura crearFactura(Factura factura, List<DetalleFactura> detalles) {
        return facturaRepository.crear(factura);
    }

    @Override
    public Factura obtenerPorId(int idFactura) {
        return facturaRepository.obtenerPorId(idFactura);
    }

    @Override
    public List<Factura> listarTodas() {
        return facturaRepository.obtenerTodas();
    }

    @Override
    public boolean eliminarFactura(int idFactura) {
        return facturaRepository.eliminar(idFactura);
    }

    @Override
    public List<Factura> obtenerPorCliente(int idCliente) {
        return facturaRepository.obtenerPorCliente(idCliente);
    }

    @Override
    public List<Factura> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return facturaRepository.obtenerPorFecha(inicio, fin);
    }

    @Override
    public byte[] generarPDF(int idFactura) {
        return new byte[0];
    }

    @Override
    public double calcularTotal(double subtotal, double descuento, double impuesto) {
        double montoDescuento = subtotal * descuento / 100;
        double baseImpuesto = subtotal - montoDescuento;
        double montoImpuesto = baseImpuesto * impuesto / 100;
        return baseImpuesto + montoImpuesto;
    }
}
