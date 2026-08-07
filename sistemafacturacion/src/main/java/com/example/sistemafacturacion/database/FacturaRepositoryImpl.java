package com.example.sistemafacturacion.database;

import com.example.sistemafacturacion.data.Factura;
import com.example.sistemafacturacion.interfaces.repository.FacturaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FacturaRepositoryImpl implements FacturaRepository {
    private final DatabaseConnection dbConnection;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FacturaRepositoryImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Factura crear(Factura factura) {
        String sql = "INSERT INTO Facturas (numeroFactura, cai, idCliente, fechaFactura, " +
                     "subtotal, descuento, impuesto, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, factura.getNumeroFactura());
            pstmt.setString(2, factura.getCai());
            pstmt.setInt(3, factura.getIdCliente());
            pstmt.setString(4, factura.getFechaFactura().format(formatter));
            pstmt.setDouble(5, factura.getSubtotal());
            pstmt.setDouble(6, factura.getDescuento());
            pstmt.setDouble(7, factura.getImpuesto());
            pstmt.setDouble(8, factura.getTotal());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factura;
    }

    @Override
    public Factura obtenerPorId(int idFactura) {
        String sql = "SELECT * FROM Facturas WHERE idFactura = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFactura);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFactura(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Factura obtenerPorNumero(int numeroFactura) {
        String sql = "SELECT * FROM Facturas WHERE numeroFactura = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numeroFactura);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFactura(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Factura> obtenerTodas() {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Facturas ORDER BY fechaFactura DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                facturas.add(mapResultSetToFactura(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facturas;
    }

    @Override
    public Factura actualizar(Factura factura) {
        String sql = "UPDATE Facturas SET numeroFactura = ?, cai = ?, idCliente = ?, " +
                     "fechaFactura = ?, subtotal = ?, descuento = ?, impuesto = ?, total = ?, estado = ? " +
                     "WHERE idFactura = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, factura.getNumeroFactura());
            pstmt.setString(2, factura.getCai());
            pstmt.setInt(3, factura.getIdCliente());
            pstmt.setString(4, factura.getFechaFactura().format(formatter));
            pstmt.setDouble(5, factura.getSubtotal());
            pstmt.setDouble(6, factura.getDescuento());
            pstmt.setDouble(7, factura.getImpuesto());
            pstmt.setDouble(8, factura.getTotal());
            pstmt.setString(9, factura.getEstado());
            pstmt.setInt(10, factura.getIdFactura());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factura;
    }

    @Override
    public boolean eliminar(int idFactura) {
        String sql = "DELETE FROM Facturas WHERE idFactura = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFactura);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Factura> obtenerPorCliente(int idCliente) {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Facturas WHERE idCliente = ? ORDER BY fechaFactura DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapResultSetToFactura(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facturas;
    }

    @Override
    public List<Factura> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Facturas WHERE fechaFactura BETWEEN ? AND ? ORDER BY fechaFactura DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inicio.format(formatter));
            pstmt.setString(2, fin.format(formatter));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapResultSetToFactura(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facturas;
    }

    @Override
    public List<Factura> obtenerPorEstado(String estado) {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Facturas WHERE estado = ? ORDER BY fechaFactura DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapResultSetToFactura(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facturas;
    }

    private Factura mapResultSetToFactura(ResultSet rs) throws SQLException {
        return new Factura(
                rs.getInt("idFactura"),
                rs.getInt("numeroFactura"),
                rs.getString("cai"),
                rs.getInt("idCliente"),
                LocalDateTime.parse(rs.getString("fechaFactura"), formatter),
                rs.getDouble("subtotal"),
                rs.getDouble("descuento"),
                rs.getDouble("impuesto"),
                rs.getDouble("total"),
                rs.getString("estado")
        );
    }
}
