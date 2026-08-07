package com.example.sistemafacturacion.interfaces.viewmodel;

import com.example.sistemafacturacion.data.Cliente;
import java.util.List;

public interface ClienteViewModel {
    void cargarClientes();
    void guardarCliente();
    void actualizarCliente();
    void eliminarCliente();
    void buscarClientes(String criterio);
    void limpiarFormulario();
    Cliente getClienteSeleccionado();
    List<Cliente> getListaClientes();
    String getNombre();
    void setNombre(String nombre);
    String getRtn();
    void setRtn(String rtn);
    String getEmail();
    void setEmail(String email);
    String getTelefono();
    void setTelefono(String telefono);
    String getDireccion();
    void setDireccion(String direccion);
}
