package com.example.sistemafacturacion.services;

import com.example.sistemafacturacion.data.Factura;
import com.example.sistemafacturacion.data.DetalleFactura;
import com.example.sistemafacturacion.database.FacturaRepositoryImpl;
import com.example.sistemafacturacion.database.DetalleFacturaRepositoryImpl;
import com.example.sistemafacturacion.database.CAIRepositoryImpl;
import com.example.sistemafacturacion.database.ProductoRepositoryImpl;
import com.example.sistemafacturacion.database.DatabaseConnection;
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
        // Orquestar inserción de factura y detalles en una sola transacción JDBC
        DatabaseConnection db = DatabaseConnection.getInstance();
        FacturaRepositoryImpl facturaRepoImpl = (FacturaRepositoryImpl) this.facturaRepository;
        DetalleFacturaRepositoryImpl detalleRepo = new DetalleFacturaRepositoryImpl();
        CAIRepositoryImpl caiRepo = new CAIRepositoryImpl();
        ProductoRepositoryImpl productoRepo = new ProductoRepositoryImpl();

        java.sql.Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            // Insertar cabecera y obtener id generado
            Factura facturaCreada = facturaRepoImpl.crearConConexion(conn, factura);

            // Insertar cada detalle con la misma conexión
            for (DetalleFactura d : detalles) {
                d.setIdFactura(facturaCreada.getIdFactura());
                detalleRepo.crearConConexion(conn, d);
                // Reducir stock usando la misma conexión
                productoRepo.actualizarStockConConexion(conn, d.getIdProducto(), d.getCantidad());
            }

            // Actualizar siguienteFactura del CAI activo
            CAIService caiService = new CAIService();
            com.example.sistemafacturacion.data.CAI caiActivo = caiService.obtenerCAIActivo();
            if (caiActivo != null) {
                int siguiente = caiActivo.getSiguienteFactura() + 1;
                caiRepo.actualizarSiguienteFactura(conn, caiActivo.getIdCAI(), siguiente);
            }

            conn.commit();
            return facturaCreada;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
            throw new RuntimeException("Error al crear factura: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
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
