package com.example.sistemafacturacion.beans;

import com.example.sistemafacturacion.data.Usuario;
import com.example.sistemafacturacion.interfaces.interactor.UsuarioInteractor;
import com.example.sistemafacturacion.services.UsuarioService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

@Named("loginBean")
@ViewScoped
public class LoginBean implements Serializable {
    private final UsuarioInteractor usuarioInteractor;
    private String nombreUsuario;
    private String contrasena;

    public LoginBean() {
        this.usuarioInteractor = new UsuarioService();
    }

    public void autenticar() {
        try {
            Usuario usuario = usuarioInteractor.autenticar(nombreUsuario, contrasena);
            if (usuario != null) {
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(false);
                session.setAttribute("usuarioAutenticado", usuario);
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("inicio.xhtml");
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                        "Usuario o contraseña incorrectos"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public String cerrarSesion() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        context.getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
