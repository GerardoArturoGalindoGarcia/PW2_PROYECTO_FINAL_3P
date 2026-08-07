package com.example.sistemafacturacion.data;

import java.time.LocalDateTime;

public class Factura {
    private int idFactura;
    private int numeroFactura;
    private String cai;
    private int idCliente;
    private LocalDateTime fechaFactura;
    private double subtotal;
    private double descuento;
    private double impuesto;
    private double total;
    private String estado;

    public Factura() {
    }

    public Factura(int idFactura, int numeroFactura, String cai, int idCliente,
                   LocalDateTime fechaFactura, double subtotal, double descuento,
                   double impuesto, double total, String estado) {
        this.idFactura = idFactura;
        this.numeroFactura = numeroFactura;
        this.cai = cai;
        this.idCliente = idCliente;
        this.fechaFactura = fechaFactura;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.impuesto = impuesto;
        this.total = total;
        this.estado = estado;
    }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public int getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(int numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getCai() { return cai; }
    public void setCai(String cai) { this.cai = cai; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public LocalDateTime getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(LocalDateTime fechaFactura) { this.fechaFactura = fechaFactura; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    public double getImpuesto() { return impuesto; }
    public void setImpuesto(double impuesto) { this.impuesto = impuesto; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
