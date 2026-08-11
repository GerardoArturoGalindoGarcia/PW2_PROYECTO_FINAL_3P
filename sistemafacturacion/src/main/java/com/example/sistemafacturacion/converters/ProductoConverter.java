package com.example.sistemafacturacion.converters;

import com.example.sistemafacturacion.data.Producto;
import com.example.sistemafacturacion.services.ProductoService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(value = "productoConverter", managed = true)
public class ProductoConverter implements Converter<Producto> {

    private final ProductoService productoService = new ProductoService();

    @Override
    public Producto getAsObject(
            FacesContext context,
            UIComponent component,
            String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            int idProducto = Integer.parseInt(value);

            Producto producto = productoService.obtenerPorId(idProducto);

            if (producto == null) {
                throw new ConverterException(
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Producto inválido",
                                "El producto seleccionado no existe"
                        )
                );
            }

            return producto;
        } catch (NumberFormatException e) {
            throw new ConverterException(
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Producto inválido",
                            "No se pudo convertir el producto seleccionado"
                    ),
                    e
            );
        }
    }

    @Override
    public String getAsString(
            FacesContext context,
            UIComponent component,
            Producto value) {

        if (value == null || value.getIdProducto() <= 0) {
            return "";
        }

        return String.valueOf(value.getIdProducto());
    }
}
