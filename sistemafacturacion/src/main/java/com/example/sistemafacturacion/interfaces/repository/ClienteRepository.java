package com.example.sistemafacturacion.interfaces.repository;

import com.example.sistemafacturacion.data.Cliente;
import java.util.List;

public interface ClienteRepository {
    Cliente crear(Cliente cliente);
    Cliente obtenerPorId(int idCliente);
    Cliente obtenerPorRtn(String rtn);
    List<Cliente> obtenerTodos();
    Cliente actualizar(Cliente cliente);
    boolean eliminar(int idCliente);
    List<Cliente> buscar(String criterio);
}
