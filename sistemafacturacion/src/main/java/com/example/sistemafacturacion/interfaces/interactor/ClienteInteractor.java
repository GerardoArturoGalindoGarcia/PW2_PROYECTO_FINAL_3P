package com.example.sistemafacturacion.interfaces.interactor;

import com.example.sistemafacturacion.data.Cliente;
import java.util.List;

public interface ClienteInteractor {
    Cliente registrarCliente(Cliente cliente);
    Cliente obtenerPorId(int idCliente);
    List<Cliente> listarTodos();
    Cliente actualizarCliente(Cliente cliente);
    boolean eliminarCliente(int idCliente);
    List<Cliente> buscarClientes(String criterio);
}
