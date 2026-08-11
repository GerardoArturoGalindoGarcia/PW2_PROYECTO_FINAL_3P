package com.example.sistemafacturacion.beans;

import com.example.sistemafacturacion.data.Usuario;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

@Named("sessionBean")
@RequestScoped
public class SessionBean implements Serializable {

    public Usuario getUsuarioActual() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            return (Usuario) session.getAttribute("usuarioAutenticado");
        }
        return null;
    }

    public boolean isAdmin() {
        Usuario u = getUsuarioActual();
        return u != null && u.getIdRol() == 1;
    }

    public String getNombreUsuarioActual() {
        Usuario u = getUsuarioActual();
        return u != null ? u.getNombre() + " " + u.getApellido() : "";
    }
}
