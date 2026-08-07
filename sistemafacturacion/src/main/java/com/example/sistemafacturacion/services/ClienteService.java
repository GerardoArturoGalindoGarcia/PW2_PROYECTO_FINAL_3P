package com.example.sistemafacturacion.services;

import com.example.sistemafacturacion.data.Cliente;
import com.example.sistemafacturacion.database.ClienteRepositoryImpl;
import com.example.sistemafacturacion.interfaces.interactor.ClienteInteractor;
import com.example.sistemafacturacion.interfaces.repository.ClienteRepository;
import java.util.List;

public class ClienteService implements ClienteInteractor {
    private final ClienteRepository clienteRepository;

    public ClienteService() {
        this.clienteRepository = new ClienteRepositoryImpl();
    }

    @Override
    public Cliente registrarCliente(Cliente cliente) {
        if (clienteRepository.obtenerPorRtn(cliente.getRtn()) != null) {
            throw new RuntimeException("El RTN del cliente ya existe");
        }
        return clienteRepository.crear(cliente);
    }

    @Override
    public Cliente obtenerPorId(int idCliente) {
        return clienteRepository.obtenerPorId(idCliente);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.obtenerTodos();
    }

    @Override
    public Cliente actualizarCliente(Cliente cliente) {
        return clienteRepository.actualizar(cliente);
    }

    @Override
    public boolean eliminarCliente(int idCliente) {
        return clienteRepository.eliminar(idCliente);
    }

    @Override
    public List<Cliente> buscarClientes(String criterio) {
        return clienteRepository.buscar(criterio);
    }
}
