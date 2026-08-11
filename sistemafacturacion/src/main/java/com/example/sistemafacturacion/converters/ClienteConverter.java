package com.example.sistemafacturacion.converters;

import com.example.sistemafacturacion.data.Cliente;
import com.example.sistemafacturacion.services.ClienteService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(value = "clienteConverter", managed = true)
public class ClienteConverter implements Converter<Cliente> {

    private final ClienteService clienteService = new ClienteService();

    @Override
    public Cliente getAsObject(
            FacesContext context,
            UIComponent component,
            String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            int idCliente = Integer.parseInt(value);
            Cliente cliente = clienteService.obtenerPorId(idCliente);

            if (cliente == null) {
                throw new ConverterException(
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Cliente inválido",
                                "El cliente seleccionado no existe"
                        )
                );
            }

            return cliente;
        } catch (NumberFormatException e) {
            throw new ConverterException(
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Cliente inválido",
                            "No se pudo convertir el cliente seleccionado"
                    ),
                    e
            );
        }
    }

    @Override
    public String getAsString(
            FacesContext context,
            UIComponent component,
            Cliente value) {

        if (value == null || value.getIdCliente() <= 0) {
            return "";
        }

        return String.valueOf(value.getIdCliente());
    }
}
