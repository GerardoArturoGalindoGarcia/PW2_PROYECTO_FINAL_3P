package com.example.sistemafacturacion.beans;

import com.example.sistemafacturacion.data.Cliente;
import com.example.sistemafacturacion.data.Usuario;
import com.example.sistemafacturacion.interfaces.interactor.ClienteInteractor;
import com.example.sistemafacturacion.interfaces.viewmodel.ClienteViewModel;
import com.example.sistemafacturacion.services.ClienteService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("clienteBean")
@ViewScoped
public class ClienteBean implements ClienteViewModel, Serializable {

    private final ClienteInteractor clienteInteractor;

    private List<Cliente> listaClientes;
    private Cliente clienteSeleccionado;

    private String nombre;
    private String rtn;
    private String email;
    private String telefono;
    private String direccion;


    public ClienteBean() {
        this.clienteInteractor = new ClienteService();
        cargarClientes();
    }


    @Override
    public void cargarClientes() {
        this.listaClientes = clienteInteractor.listarTodos();
    }


    @Override
    public void guardarCliente() {

        try {

            Cliente cliente = new Cliente();

            cliente.setNombre(nombre);
            cliente.setRtn(rtn);
            cliente.setEmail(email);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            cliente.setEstado("activo");

            clienteInteractor.registrarCliente(cliente);

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Éxito",
                            "Cliente registrado correctamente"
                    )
            );

            limpiarFormulario();
            cargarClientes();

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            e.getMessage()
                    )
            );
        }
    }


    @Override
    public void actualizarCliente() {

        if (clienteSeleccionado != null) {

            try {

                clienteSeleccionado.setNombre(nombre);
                clienteSeleccionado.setRtn(rtn);
                clienteSeleccionado.setEmail(email);
                clienteSeleccionado.setTelefono(telefono);
                clienteSeleccionado.setDireccion(direccion);

                clienteInteractor.actualizarCliente(clienteSeleccionado);

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Éxito",
                                "Cliente actualizado correctamente"
                        )
                );

                limpiarFormulario();
                clienteSeleccionado = null;
                cargarClientes();

            } catch (Exception e) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                e.getMessage()
                        )
                );
            }
        }
    }


    @Override
    public void eliminarCliente() {
        if (clienteSeleccionado != null) {

            try {

                clienteInteractor.eliminarCliente(
                        clienteSeleccionado.getIdCliente()
                );

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Éxito",
                                "Cliente eliminado correctamente"
                        )
                );

                clienteSeleccionado = null;
                cargarClientes();

            } catch (Exception e) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                e.getMessage()
                        )
                );
            }
        }
    }

    public void eliminarCliente(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        eliminarCliente();
    }


    @Override
    public void buscarClientes(String criterio) {
        this.listaClientes = clienteInteractor.buscarClientes(criterio);
    }


    @Override
    public void limpiarFormulario() {

        nombre = null;
        rtn = null;
        email = null;
        telefono = null;
        direccion = null;

        clienteSeleccionado = null;
    }


    public void guardarOActualizarCliente() {
        if (clienteSeleccionado != null) {
            actualizarCliente();
        } else {
            guardarCliente();
        }
    }


    // =========================
    // SELECCIONAR CLIENTE
    // =========================

    public void seleccionarCliente(Cliente cliente) {

        this.clienteSeleccionado = cliente;

        this.nombre = cliente.getNombre();
        this.rtn = cliente.getRtn();
        this.email = cliente.getEmail();
        this.telefono = cliente.getTelefono();
        this.direccion = cliente.getDireccion();
    }


    // =========================
    // GETTERS Y SETTERS
    // =========================

    @Override
    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }


    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
    }


    @Override
    public List<Cliente> getListaClientes() {
        return listaClientes;
    }


    @Override
    public String getNombre() {
        return nombre;
    }


    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    @Override
    public String getRtn() {
        return rtn;
    }


    @Override
    public void setRtn(String rtn) {
        this.rtn = rtn;
    }


    @Override
    public String getEmail() {
        return email;
    }


    @Override
    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String getTelefono() {
        return telefono;
    }


    @Override
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    @Override
    public String getDireccion() {
        return direccion;
    }


    @Override
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}