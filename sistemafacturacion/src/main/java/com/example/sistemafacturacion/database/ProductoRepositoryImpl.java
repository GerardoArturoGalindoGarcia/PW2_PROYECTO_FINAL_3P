package com.example.sistemafacturacion.database;

import com.example.sistemafacturacion.data.Producto;
import com.example.sistemafacturacion.interfaces.repository.ProductoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {
    private final DatabaseConnection dbConnection;

    public ProductoRepositoryImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Producto crear(Producto producto) {
        String sql = "INSERT INTO Productos (nombre, descripcion, precioVenta, stock) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setInt(4, producto.getStock());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producto;
    }

    @Override
    public Producto obtenerPorId(int idProducto) {
        String sql = "SELECT * FROM Productos WHERE idProducto = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProducto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Productos WHERE estado = 'activo'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    @Override
    public Producto actualizar(Producto producto) {
        String sql = "UPDATE Productos SET nombre = ?, descripcion = ?, precioVenta = ?, " +
                     "stock = ?, estado = ? WHERE idProducto = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setInt(4, producto.getStock());
            pstmt.setString(5, producto.getEstado());
            pstmt.setInt(6, producto.getIdProducto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producto;
    }

    @Override
    public boolean eliminar(int idProducto) {
        String sql = "UPDATE Productos SET estado = 'inactivo' WHERE idProducto = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Producto> buscar(String criterio) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Productos WHERE nombre LIKE ? OR descripcion LIKE ? " +
                     "AND estado = 'activo'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String criterioLike = "%" + criterio + "%";
            pstmt.setString(1, criterioLike);
            pstmt.setString(2, criterioLike);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    @Override
    public void actualizarStock(int idProducto, int cantidad) {
        String sql = "UPDATE Productos SET stock = stock - ? WHERE idProducto = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("idProducto"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precioVenta"),
                rs.getInt("stock"),
                rs.getString("estado")
        );
    }
}
