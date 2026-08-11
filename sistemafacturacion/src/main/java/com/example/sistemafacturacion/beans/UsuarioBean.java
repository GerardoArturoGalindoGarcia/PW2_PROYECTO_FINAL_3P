package com.example.sistemafacturacion.beans;

import com.example.sistemafacturacion.data.Usuario;
import com.example.sistemafacturacion.interfaces.interactor.UsuarioInteractor;
import com.example.sistemafacturacion.interfaces.viewmodel.UsuarioViewModel;
import com.example.sistemafacturacion.services.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Named("usuarioBean")
@ViewScoped
public class UsuarioBean implements UsuarioViewModel, Serializable {
    private final UsuarioInteractor usuarioInteractor;
    private List<Usuario> listaUsuarios;
    private Usuario usuarioSeleccionado;
    private String nombreUsuario;
    private String contrasena;
    private String nombre;
    private String apellido;
    private String email;
    private int idRolNuevo = 2;

    public UsuarioBean() {
        this.usuarioInteractor = new UsuarioService();
        cargarUsuarios();
    }

    @PostConstruct
    public void init() {
        if (!esAdmin()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("inicio.xhtml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean esAdmin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            Usuario u = (Usuario) session.getAttribute("usuarioAutenticado");
            return u != null && u.getIdRol() == 1;
        }
        return false;
    }

    @Override
    public void cargarUsuarios() {
        this.listaUsuarios = usuarioInteractor.listarTodos();
    }

    @Override
    public void guardarUsuario() {
        if (!esAdmin()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acceso denegado",
                            "Solo el administrador puede gestionar usuarios"));
            return;
        }
        // Si hay un usuario seleccionado (modo edición), actualiza en vez de insertar
        if (usuarioSeleccionado != null) {
            actualizarUsuario();
            return;
        }
        try {
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(nombreUsuario);
            usuario.setContrasena(contrasena);
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setIdRol(idRolNuevo);
            usuario.setEstado("activo");
            usuarioInteractor.registrarUsuario(usuario);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Éxito", "Usuario registrado correctamente"));
            limpiarFormulario();
            cargarUsuarios();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // Carga los datos del usuario elegido en la fila hacia los campos del formulario (modo edición)
    public void editarUsuario(Usuario usuario) {
        this.usuarioSeleccionado = usuario;
        this.nombreUsuario = usuario.getNombreUsuario();
        this.contrasena = usuario.getContrasena();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.idRolNuevo = usuario.getIdRol();
    }

    @Override
    public void actualizarUsuario() {
        if (usuarioSeleccionado != null) {
            try {
                usuarioSeleccionado.setNombreUsuario(nombreUsuario);
                usuarioSeleccionado.setContrasena(contrasena);
                usuarioSeleccionado.setNombre(nombre);
                usuarioSeleccionado.setApellido(apellido);
                usuarioSeleccionado.setEmail(email);
                usuarioInteractor.actualizarUsuario(usuarioSeleccionado);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Éxito", "Usuario actualizado correctamente"));
                limpiarFormulario();
                cargarUsuarios();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }
    }

    @Override
    public void eliminarUsuario() {
        if (usuarioSeleccionado != null) {
            try {
                usuarioInteractor.eliminarUsuario(usuarioSeleccionado.getIdUsuario());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Éxito", "Usuario eliminado correctamente"));
                limpiarFormulario();
                cargarUsuarios();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }
    }

    // Sobrecarga usada desde el botón "Eliminar" de la tabla: fija la selección y borra en un solo paso
    public void eliminarUsuario(Usuario usuario) {
        this.usuarioSeleccionado = usuario;
        eliminarUsuario();
    }

    @Override
    public void limpiarFormulario() {
        usuarioSeleccionado = null;
        nombreUsuario = null;
        contrasena = null;
        nombre = null;
        apellido = null;
        email = null;
        idRolNuevo = 2;
    }

    // Getters and Setters
    @Override
    public Usuario getUsuarioSeleccionado() { return usuarioSeleccionado; }
    public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }
    @Override
    public List<Usuario> getListaUsuarios() { return listaUsuarios; }
    @Override
    public String getNombreUsuario() { return nombreUsuario; }
    @Override
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    @Override
    public String getContrasena() { return contrasena; }
    @Override
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    @Override
    public String getNombre() { return nombre; }
    @Override
    public void setNombre(String nombre) { this.nombre = nombre; }
    @Override
    public String getApellido() { return apellido; }
    @Override
    public void setApellido(String apellido) { this.apellido = apellido; }
    @Override
    public String getEmail() { return email; }
    @Override
    public void setEmail(String email) { this.email = email; }
    public int getIdRolNuevo() { return idRolNuevo; }
    public void setIdRolNuevo(int idRolNuevo) { this.idRolNuevo = idRolNuevo; }
}
