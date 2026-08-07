package com.example.sistemafacturacion.beans;

import com.example.sistemafacturacion.data.CAI;
import com.example.sistemafacturacion.interfaces.interactor.CAIInteractor;
import com.example.sistemafacturacion.services.CAIService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named("caiBean")
@ViewScoped
public class CAIBean implements Serializable {
    private final CAIInteractor caiInteractor;
    private List<CAI> listaCAI;
    private CAI caiSeleccionado;
    private String codigoCAI;
    private String rtn;
    private int rangoInicial;
    private int rangoFinal;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;

    public CAIBean() {
        this.caiInteractor = new CAIService();
        cargarCAI();
    }

    public void cargarCAI() {
        this.listaCAI = caiInteractor.listarTodas();
    }

    public void guardarCAI() {
        try {
            CAI cai = new CAI();
            cai.setCai(codigoCAI);
            cai.setRtn(rtn);
            cai.setRangoInicial(rangoInicial);
            cai.setRangoFinal(rangoFinal);
            cai.setSiguienteFactura(rangoInicial);
            cai.setFechaEmision(fechaEmision);
            cai.setFechaVencimiento(fechaVencimiento);
            cai.setEstado("activo");
            caiInteractor.registrarCAI(cai);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Éxito", "CAI registrado correctamente"));
            limpiarFormulario();
            cargarCAI();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void actualizarCAI() {
        if (caiSeleccionado != null) {
            try {
                caiInteractor.actualizarCAI(caiSeleccionado);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Éxito", "CAI actualizado correctamente"));
                cargarCAI();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }
    }

    public void eliminarCAI() {
        if (caiSeleccionado != null) {
            try {
                caiInteractor.eliminarCAI(caiSeleccionado.getIdCAI());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Éxito", "CAI eliminado correctamente"));
                cargarCAI();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }
    }

    public void limpiarFormulario() {
        codigoCAI = null;
        rtn = null;
        rangoInicial = 0;
        rangoFinal = 0;
        fechaEmision = null;
        fechaVencimiento = null;
    }

    // Getters and Setters
    public List<CAI> getListaCAI() { return listaCAI; }
    public CAI getCaiSeleccionado() { return caiSeleccionado; }
    public void setCaiSeleccionado(CAI caiSeleccionado) { this.caiSeleccionado = caiSeleccionado; }
    public String getCodigoCAI() { return codigoCAI; }
    public void setCodigoCAI(String codigoCAI) { this.codigoCAI = codigoCAI; }
    public String getRtn() { return rtn; }
    public void setRtn(String rtn) { this.rtn = rtn; }
    public int getRangoInicial() { return rangoInicial; }
    public void setRangoInicial(int rangoInicial) { this.rangoInicial = rangoInicial; }
    public int getRangoFinal() { return rangoFinal; }
    public void setRangoFinal(int rangoFinal) { this.rangoFinal = rangoFinal; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
