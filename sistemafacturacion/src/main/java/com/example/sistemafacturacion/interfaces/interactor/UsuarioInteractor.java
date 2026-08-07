package com.example.sistemafacturacion.interfaces.interactor;

import com.example.sistemafacturacion.data.Usuario;
import java.util.List;

public interface UsuarioInteractor {
    Usuario registrarUsuario(Usuario usuario);
    Usuario autenticar(String nombreUsuario, String contrasena);
    Usuario obtenerPorId(int idUsuario);
    List<Usuario> listarTodos();
    Usuario actualizarUsuario(Usuario usuario);
    boolean cambiarContrasena(int idUsuario, String contrasenaActual, String nuevaContrasena);
    boolean eliminarUsuario(int idUsuario);
}
