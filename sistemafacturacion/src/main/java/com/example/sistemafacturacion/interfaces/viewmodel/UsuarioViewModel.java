package com.example.sistemafacturacion.interfaces.viewmodel;

import com.example.sistemafacturacion.data.Usuario;
import java.util.List;

public interface UsuarioViewModel {
    void cargarUsuarios();
    void guardarUsuario();
    void actualizarUsuario();
    void eliminarUsuario();
    void limpiarFormulario();
    Usuario getUsuarioSeleccionado();
    List<Usuario> getListaUsuarios();
    String getNombreUsuario();
    void setNombreUsuario(String nombreUsuario);
    String getContrasena();
    void setContrasena(String contrasena);
    String getNombre();
    void setNombre(String nombre);
    String getApellido();
    void setApellido(String apellido);
    String getEmail();
    void setEmail(String email);
}
