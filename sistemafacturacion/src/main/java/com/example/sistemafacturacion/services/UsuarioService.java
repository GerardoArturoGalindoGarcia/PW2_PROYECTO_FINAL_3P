package com.example.sistemafacturacion.services;

import com.example.sistemafacturacion.data.Usuario;
import com.example.sistemafacturacion.database.UsuarioRepositoryImpl;
import com.example.sistemafacturacion.interfaces.interactor.UsuarioInteractor;
import com.example.sistemafacturacion.interfaces.repository.UsuarioRepository;
import java.util.List;

public class UsuarioService implements UsuarioInteractor {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existePorNombreUsuario(usuario.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        return usuarioRepository.crear(usuario);
    }

    @Override
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        Usuario usuario = usuarioRepository.obtenerPorNombreUsuario(nombreUsuario);
        if (usuario == null || !usuario.getContrasena().equals(contrasena)) {
            return null;
        }
        if (!"activo".equals(usuario.getEstado())) {
            return null;
        }
        return usuario;
    }

    @Override
    public Usuario obtenerPorId(int idUsuario) {
        return usuarioRepository.obtenerPorId(idUsuario);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.obtenerTodos();
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.actualizar(usuario);
    }

    @Override
    public boolean cambiarContrasena(int idUsuario, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.obtenerPorId(idUsuario);
        if (usuario == null || !usuario.getContrasena().equals(contrasenaActual)) {
            return false;
        }
        usuario.setContrasena(nuevaContrasena);
        usuarioRepository.actualizar(usuario);
        return true;
    }

    @Override
    public boolean eliminarUsuario(int idUsuario) {
        return usuarioRepository.eliminar(idUsuario);
    }
}
