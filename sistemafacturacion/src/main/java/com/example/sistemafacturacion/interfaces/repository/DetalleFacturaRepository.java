package com.example.sistemafacturacion.interfaces.repository;

import com.example.sistemafacturacion.data.DetalleFactura;
import java.sql.Connection;
import java.util.List;

public interface DetalleFacturaRepository {
    DetalleFactura crear(DetalleFactura detalle);
    List<DetalleFactura> obtenerPorFactura(int idFactura);
    // métodos que permiten usar una conexión externa para transacciones
    DetalleFactura crearConConexion(Connection conn, DetalleFactura detalle) throws Exception;
    List<DetalleFactura> obtenerPorFacturaConConexion(Connection conn, int idFactura) throws Exception;
}
