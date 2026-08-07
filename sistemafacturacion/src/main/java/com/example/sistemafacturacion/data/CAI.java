package com.example.sistemafacturacion.data;

import java.time.LocalDate;

public class CAI {
    private int idCAI;
    private String cai;
    private String rtn;
    private int rangoInicial;
    private int rangoFinal;
    private int siguienteFactura;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String estado;

    public CAI() {
    }

    public CAI(int idCAI, String cai, String rtn, int rangoInicial, int rangoFinal,
               int siguienteFactura, LocalDate fechaEmision, LocalDate fechaVencimiento, String estado) {
        this.idCAI = idCAI;
        this.cai = cai;
        this.rtn = rtn;
        this.rangoInicial = rangoInicial;
        this.rangoFinal = rangoFinal;
        this.siguienteFactura = siguienteFactura;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public int getIdCAI() { return idCAI; }
    public void setIdCAI(int idCAI) { this.idCAI = idCAI; }
    public String getCai() { return cai; }
    public void setCai(String cai) { this.cai = cai; }
    public String getRtn() { return rtn; }
    public void setRtn(String rtn) { this.rtn = rtn; }
    public int getRangoInicial() { return rangoInicial; }
    public void setRangoInicial(int rangoInicial) { this.rangoInicial = rangoInicial; }
    public int getRangoFinal() { return rangoFinal; }
    public void setRangoFinal(int rangoFinal) { this.rangoFinal = rangoFinal; }
    public int getSiguienteFactura() { return siguienteFactura; }
    public void setSiguienteFactura(int siguienteFactura) { this.siguienteFactura = siguienteFactura; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
