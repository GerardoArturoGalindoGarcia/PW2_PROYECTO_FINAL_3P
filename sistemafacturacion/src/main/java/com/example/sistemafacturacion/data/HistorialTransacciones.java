package com.example.sistemafacturacion.data;

import java.time.LocalDateTime;

public class HistorialTransacciones {
    private int idTransaccion;
    private int idFactura;
    private int idUsuario;
    private LocalDateTime fechaTransaccion;
    private String tipoTransaccion;
    private String descripcion;

    public HistorialTransacciones() {
    }

    public HistorialTransacciones(int idTransaccion, int idFactura, int idUsuario,
                                  LocalDateTime fechaTransaccion, String tipoTransaccion, String descripcion) {
        this.idTransaccion = idTransaccion;
        this.idFactura = idFactura;
        this.idUsuario = idUsuario;
        this.fechaTransaccion = fechaTransaccion;
        this.tipoTransaccion = tipoTransaccion;
        this.descripcion = descripcion;
    }

    public int getIdTransaccion() { return idTransaccion; }
    public void setIdTransaccion(int idTransaccion) { this.idTransaccion = idTransaccion; }
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public LocalDateTime getFechaTransaccion() { return fechaTransaccion; }
    public void setFechaTransaccion(LocalDateTime fechaTransaccion) { this.fechaTransaccion = fechaTransaccion; }
    public String getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(String tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
