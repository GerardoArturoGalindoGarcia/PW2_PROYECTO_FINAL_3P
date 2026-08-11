package com.example.sistemafacturacion.database;

import com.example.sistemafacturacion.data.CAI;
import com.example.sistemafacturacion.interfaces.repository.CAIRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CAIRepositoryImpl implements CAIRepository {
    private final DatabaseConnection dbConnection;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public CAIRepositoryImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public CAI crear(CAI cai) {
        String sql = "INSERT INTO CAI (cai, rtn, rangoInicial, rangoFinal, siguienteFactura, " +
                     "fechaEmision, fechaVencimiento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cai.getCai());
            pstmt.setString(2, cai.getRtn());
            pstmt.setInt(3, cai.getRangoInicial());
            pstmt.setInt(4, cai.getRangoFinal());
            pstmt.setInt(5, cai.getSiguienteFactura());
            pstmt.setString(6, cai.getFechaEmision().format(formatter));
            pstmt.setString(7, cai.getFechaVencimiento().format(formatter));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cai;
    }

    @Override
    public CAI obtenerPorId(int idCAI) {
        String sql = "SELECT * FROM CAI WHERE idCAI = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCAI);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCAI(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CAI obtenerPorCodigoCAI(String codigoCAI) {
        String sql = "SELECT * FROM CAI WHERE cai = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigoCAI);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCAI(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CAI> obtenerTodas() {
        List<CAI> cais = new ArrayList<>();
        String sql = "SELECT * FROM CAI";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                cais.add(mapResultSetToCAI(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cais;
    }

    @Override
    public CAI actualizar(CAI cai) {
        String sql = "UPDATE CAI SET cai = ?, rtn = ?, rangoInicial = ?, rangoFinal = ?, " +
                     "siguienteFactura = ?, fechaEmision = ?, fechaVencimiento = ?, estado = ? " +
                     "WHERE idCAI = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cai.getCai());
            pstmt.setString(2, cai.getRtn());
            pstmt.setInt(3, cai.getRangoInicial());
            pstmt.setInt(4, cai.getRangoFinal());
            pstmt.setInt(5, cai.getSiguienteFactura());
            pstmt.setString(6, cai.getFechaEmision().format(formatter));
            pstmt.setString(7, cai.getFechaVencimiento().format(formatter));
            pstmt.setString(8, cai.getEstado());
            pstmt.setInt(9, cai.getIdCAI());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cai;
    }

    @Override
    public boolean eliminar(int idCAI) {
        String sql = "DELETE FROM CAI WHERE idCAI = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCAI);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CAI obtenerCAIActivo() {
        String sql = "SELECT * FROM CAI WHERE estado = 'activo' LIMIT 1";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToCAI(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CAI obtenerCAIActivo(Connection conn) throws SQLException {
        String sql = "SELECT * FROM CAI WHERE estado = 'activo' LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToCAI(rs);
            }
        }
        return null;
    }

    @Override
    public void actualizarSiguienteFactura(int idCAI, int siguiente) {
        // Delegar a la versión con Connection propia
        try (Connection conn = dbConnection.getConnection()) {
            actualizarSiguienteFactura(conn, idCAI, siguiente);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Versión que participa en transacciones externas
    public void actualizarSiguienteFactura(Connection conn, int idCAI, int siguiente) throws SQLException {
        String sql = "UPDATE CAI SET siguienteFactura = ? WHERE idCAI = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, siguiente);
            pstmt.setInt(2, idCAI);
            pstmt.executeUpdate();
        }
    }

    private CAI mapResultSetToCAI(ResultSet rs) throws SQLException {
        return new CAI(
                rs.getInt("idCAI"),
                rs.getString("cai"),
                rs.getString("rtn"),
                rs.getInt("rangoInicial"),
                rs.getInt("rangoFinal"),
                rs.getInt("siguienteFactura"),
                LocalDate.parse(rs.getString("fechaEmision"), formatter),
                LocalDate.parse(rs.getString("fechaVencimiento"), formatter),
                rs.getString("estado")
        );
    }
}
