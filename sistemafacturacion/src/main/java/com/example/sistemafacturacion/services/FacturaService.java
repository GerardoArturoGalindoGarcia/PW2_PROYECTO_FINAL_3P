package com.example.sistemafacturacion.services;

import com.example.sistemafacturacion.data.Factura;
import com.example.sistemafacturacion.data.DetalleFactura;
import com.example.sistemafacturacion.data.CAI;
import com.example.sistemafacturacion.database.FacturaRepositoryImpl;
import com.example.sistemafacturacion.data.Cliente;
import com.example.sistemafacturacion.database.ClienteRepositoryImpl;
import com.example.sistemafacturacion.database.DetalleFacturaRepositoryImpl;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;
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
            CAI caiActivo = caiRepo.obtenerCAIActivo(conn);
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
        try {
            Factura factura = facturaRepository.obtenerPorId(idFactura);
            if (factura == null) throw new RuntimeException("Factura no encontrada");

            List<DetalleFactura> detalles =
                    new DetalleFacturaRepositoryImpl().obtenerPorFactura(idFactura);

            Cliente cliente = null;
            if (factura.getIdCliente() > 0) {
                cliente = new ClienteRepositoryImpl().obtenerPorId(factura.getIdCliente());
            }

            NumberFormat nf = NumberFormat.getInstance(new Locale("es", "HN"));
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font fontTitulo  = new Font(Font.HELVETICA, 20, Font.BOLD, Color.WHITE);
            Font fontSub     = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.WHITE);
            Font fontLabel   = new Font(Font.HELVETICA, 9,  Font.BOLD);
            Font fontValor   = new Font(Font.HELVETICA, 9,  Font.NORMAL);
            Font fontHeader  = new Font(Font.HELVETICA, 9,  Font.BOLD, Color.WHITE);
            Font fontCell    = new Font(Font.HELVETICA, 9,  Font.NORMAL);
            Font fontTotal   = new Font(Font.HELVETICA, 10, Font.BOLD);

            Color azul       = new Color(30, 80, 160);
            Color azulClaro  = new Color(220, 230, 245);
            Color grisClaro  = new Color(245, 245, 245);

            // ── Encabezado ──────────────────────────────────────────────
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{60, 40});

            PdfPCell celdaTitulo = new PdfPCell();
            celdaTitulo.setBackgroundColor(azul);
            celdaTitulo.setPadding(12);
            celdaTitulo.setBorder(Rectangle.NO_BORDER);
            celdaTitulo.addElement(new Paragraph("FACTURA", fontTitulo));
            celdaTitulo.addElement(new Paragraph("Sistema de Facturación", fontSub));
            header.addCell(celdaTitulo);

            PdfPCell celdaNumero = new PdfPCell();
            celdaNumero.setBackgroundColor(azulClaro);
            celdaNumero.setPadding(12);
            celdaNumero.setBorder(Rectangle.NO_BORDER);
            celdaNumero.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Font fNum = new Font(Font.HELVETICA, 14, Font.BOLD, azul);
            celdaNumero.addElement(new Paragraph("N° " + factura.getNumeroFactura(), fNum));
            Font fFecha = new Font(Font.HELVETICA, 9, Font.NORMAL);
            celdaNumero.addElement(new Paragraph("Fecha: " + factura.getFechaFacturaFormateada(), fFecha));
            celdaNumero.addElement(new Paragraph("CAI: " + factura.getCai(), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            header.addCell(celdaNumero);

            doc.add(header);
            doc.add(Chunk.NEWLINE);

            // ── Datos del cliente ────────────────────────────────────────
            PdfPTable tCliente = new PdfPTable(2);
            tCliente.setWidthPercentage(100);
            tCliente.setWidths(new float[]{20, 80});

            PdfPCell lblCliente = new PdfPCell(new Phrase("CLIENTE", fontHeader));
            lblCliente.setBackgroundColor(azul);
            lblCliente.setColspan(2);
            lblCliente.setPadding(6);
            lblCliente.setBorder(Rectangle.NO_BORDER);
            tCliente.addCell(lblCliente);

            String nombreCliente = (cliente != null) ? cliente.getNombre() : "Consumidor Final";
            String rtnCliente    = (cliente != null && cliente.getRtn() != null) ? cliente.getRtn() : "—";
            String emailCliente  = (cliente != null && cliente.getEmail() != null) ? cliente.getEmail() : "—";

            agregarFilaInfo(tCliente, "Nombre:", nombreCliente, fontLabel, fontValor, grisClaro, false);
            agregarFilaInfo(tCliente, "RTN:",    rtnCliente,    fontLabel, fontValor, Color.WHITE, false);
            agregarFilaInfo(tCliente, "Email:",  emailCliente,  fontLabel, fontValor, grisClaro, true);

            doc.add(tCliente);
            doc.add(Chunk.NEWLINE);

            // ── Detalle de productos ─────────────────────────────────────
            PdfPTable tDetalle = new PdfPTable(5);
            tDetalle.setWidthPercentage(100);
            tDetalle.setWidths(new float[]{35, 12, 18, 18, 17});

            String[] colHeaders = {"Producto", "Cant.", "Precio Unit.", "Subtotal", ""};
            for (String h : colHeaders) {
                PdfPCell ch = new PdfPCell(new Phrase(h, fontHeader));
                ch.setBackgroundColor(azul);
                ch.setPadding(6);
                ch.setBorder(Rectangle.NO_BORDER);
                ch.setHorizontalAlignment(Element.ALIGN_CENTER);
                tDetalle.addCell(ch);
            }

            boolean alt = false;
            for (DetalleFactura d : detalles) {
                Color bg = alt ? grisClaro : Color.WHITE;
                String nombre = (d.getNombreProducto() != null && !d.getNombreProducto().isEmpty())
                        ? d.getNombreProducto() : "ID " + d.getIdProducto();

                tDetalle.addCell(celda(nombre,                           fontCell, bg, Element.ALIGN_LEFT));
                tDetalle.addCell(celda(String.valueOf(d.getCantidad()),   fontCell, bg, Element.ALIGN_CENTER));
                tDetalle.addCell(celda("L " + nf.format(d.getPrecioUnitario()), fontCell, bg, Element.ALIGN_RIGHT));
                tDetalle.addCell(celda("L " + nf.format(d.getSubtotal()),       fontCell, bg, Element.ALIGN_RIGHT));
                tDetalle.addCell(celda("",                                fontCell, bg, Element.ALIGN_LEFT));
                alt = !alt;
            }
            doc.add(tDetalle);
            doc.add(Chunk.NEWLINE);

            // ── Resumen financiero ───────────────────────────────────────
            PdfPTable tTotales = new PdfPTable(2);
            tTotales.setWidthPercentage(50);
            tTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tTotales.setWidths(new float[]{55, 45});

            double montoDesc = factura.getSubtotal() * factura.getDescuento() / 100.0;
            double montoISV  = (factura.getSubtotal() - montoDesc) * factura.getImpuesto() / 100.0;

            agregarFilaTotales(tTotales, "Subtotal:",
                    "L " + nf.format(factura.getSubtotal()), fontLabel, fontValor, Color.WHITE);
            agregarFilaTotales(tTotales,
                    "Descuento (" + nf.format(factura.getDescuento()) + "%):",
                    "- L " + nf.format(montoDesc), fontLabel, fontValor, grisClaro);
            agregarFilaTotales(tTotales,
                    "ISV (" + (int) factura.getImpuesto() + "%):",
                    "L " + nf.format(montoISV), fontLabel, fontValor, Color.WHITE);

            PdfPCell lblTotal = new PdfPCell(new Phrase("TOTAL:", fontTotal));
            lblTotal.setBackgroundColor(azulClaro);
            lblTotal.setPadding(7);
            lblTotal.setBorder(Rectangle.NO_BORDER);
            tTotales.addCell(lblTotal);

            Font fontTotalVal = new Font(Font.HELVETICA, 10, Font.BOLD, azul);
            PdfPCell valTotal = new PdfPCell(new Phrase("L " + nf.format(factura.getTotal()), fontTotalVal));
            valTotal.setBackgroundColor(azulClaro);
            valTotal.setPadding(7);
            valTotal.setBorder(Rectangle.NO_BORDER);
            valTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tTotales.addCell(valTotal);

            doc.add(tTotales);

            // ── Pie de página ────────────────────────────────────────────
            doc.add(Chunk.NEWLINE);
            Font fontPie = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);
            Paragraph pie = new Paragraph("Documento generado electrónicamente. CAI: " + factura.getCai(), fontPie);
            pie.setAlignment(Element.ALIGN_CENTER);
            doc.add(pie);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }

    private void agregarFilaInfo(PdfPTable t, String label, String valor,
                                  Font fLabel, Font fValor, Color bg, boolean lastRow) {
        PdfPCell l = new PdfPCell(new Phrase(label, fLabel));
        l.setBackgroundColor(bg);
        l.setPadding(5);
        l.setBorder(Rectangle.NO_BORDER);
        t.addCell(l);
        PdfPCell v = new PdfPCell(new Phrase(valor, fValor));
        v.setBackgroundColor(bg);
        v.setPadding(5);
        v.setBorder(Rectangle.NO_BORDER);
        t.addCell(v);
    }

    private PdfPCell celda(String texto, Font font, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setBackgroundColor(bg);
        c.setPadding(5);
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(align);
        return c;
    }

    private void agregarFilaTotales(PdfPTable t, String label, String valor,
                                     Font fLabel, Font fValor, Color bg) {
        PdfPCell l = new PdfPCell(new Phrase(label, fLabel));
        l.setBackgroundColor(bg);
        l.setPadding(5);
        l.setBorder(Rectangle.NO_BORDER);
        t.addCell(l);
        PdfPCell v = new PdfPCell(new Phrase(valor, fValor));
        v.setBackgroundColor(bg);
        v.setPadding(5);
        v.setBorder(Rectangle.NO_BORDER);
        v.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(v);
    }

    @Override
    public double calcularTotal(double subtotal, double descuento, double impuesto) {
        double montoDescuento = subtotal * descuento / 100;
        double baseImpuesto = subtotal - montoDescuento;
        double montoImpuesto = baseImpuesto * impuesto / 100;
        return baseImpuesto + montoImpuesto;
    }
}
