package com.example.sistemafacturacion.database;

import com.example.sistemafacturacion.data.DetalleFactura;
import com.example.sistemafacturacion.interfaces.repository.DetalleFacturaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleFacturaRepositoryImpl implements DetalleFacturaRepository {
    private final DatabaseConnection dbConnection;

    public DetalleFacturaRepositoryImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public DetalleFactura crear(DetalleFactura detalle) {
        try (Connection conn = dbConnection.getConnection()) {
            return crearConConexion(conn, detalle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detalle;
    }

    @Override
    public DetalleFactura crearConConexion(Connection conn, DetalleFactura detalle) throws Exception {
        String sql = "INSERT INTO DetalleFacturas (idFactura, idProducto, cantidad, precioUnitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, detalle.getIdFactura());
            pstmt.setInt(2, detalle.getIdProducto());
            pstmt.setInt(3, detalle.getCantidad());
            pstmt.setDouble(4, detalle.getPrecioUnitario());
            pstmt.setDouble(5, detalle.getSubtotal());
            int affected = pstmt.executeUpdate();
            if (affected == 0) throw new SQLException("Crear detalle falló, ninguna fila afectada.");
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    detalle.setIdDetalle(keys.getInt(1));
                }
            }
        }
        return detalle;
    }

    @Override
    public List<DetalleFactura> obtenerPorFactura(int idFactura) {
        List<DetalleFactura> detalles = new ArrayList<>();
        String sql = "SELECT df.*, p.nombre AS productoNombre " +
                "FROM DetalleFacturas df " +
                "LEFT JOIN Productos p ON df.idProducto = p.idProducto " +
                "WHERE df.idFactura = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFactura);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleFactura d = new DetalleFactura();
                    d.setIdDetalle(rs.getInt("idDetalle"));
                    d.setIdFactura(rs.getInt("idFactura"));
                    d.setIdProducto(rs.getInt("idProducto"));
                    d.setNombreProducto(rs.getString("productoNombre"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precioUnitario"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    detalles.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    @Override
    public List<DetalleFactura> obtenerPorFacturaConConexion(Connection conn, int idFactura) throws Exception {
        List<DetalleFactura> detalles = new ArrayList<>();
        String sql = "SELECT df.*, p.nombre as productoNombre FROM DetalleFacturas df " +
                     "LEFT JOIN Productos p ON df.idProducto = p.idProducto WHERE df.idFactura = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFactura);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleFactura d = new DetalleFactura();
                    d.setIdDetalle(rs.getInt("idDetalle"));
                    d.setIdFactura(rs.getInt("idFactura"));
                    d.setIdProducto(rs.getInt("idProducto"));
                    d.setNombreProducto(rs.getString("productoNombre"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precioUnitario"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    detalles.add(d);
                }
            }
        }
        return detalles;
    }
}
