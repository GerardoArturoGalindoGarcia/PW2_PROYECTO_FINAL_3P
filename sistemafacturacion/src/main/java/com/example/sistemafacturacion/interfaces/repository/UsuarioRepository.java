package com.example.sistemafacturacion.interfaces.repository;

import com.example.sistemafacturacion.data.Usuario;
import java.util.List;

public interface UsuarioRepository {
    Usuario crear(Usuario usuario);
    Usuario obtenerPorId(int idUsuario);
    Usuario obtenerPorNombreUsuario(String nombreUsuario);
    List<Usuario> obtenerTodos();
    Usuario actualizar(Usuario usuario);
    boolean eliminar(int idUsuario);
    boolean existePorNombreUsuario(String nombreUsuario);
}
