package com.example.sistemafacturacion.interfaces.repository;

import com.example.sistemafacturacion.data.Factura;
import java.time.LocalDateTime;
import java.util.List;

public interface FacturaRepository {
    Factura crear(Factura factura);
    Factura obtenerPorId(int idFactura);
    Factura obtenerPorNumero(int numeroFactura);
    List<Factura> obtenerTodas();
    Factura actualizar(Factura factura);
    boolean eliminar(int idFactura);
    List<Factura> obtenerPorCliente(int idCliente);
    List<Factura> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin);
    List<Factura> obtenerPorEstado(String estado);
}
