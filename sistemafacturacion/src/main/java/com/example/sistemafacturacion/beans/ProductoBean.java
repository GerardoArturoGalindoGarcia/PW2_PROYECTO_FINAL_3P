package com.example.sistemafacturacion.beans;

import com.example.sistemafacturacion.data.Producto;
import com.example.sistemafacturacion.interfaces.interactor.ProductoInteractor;
import com.example.sistemafacturacion.interfaces.viewmodel.ProductoViewModel;
import com.example.sistemafacturacion.services.ProductoService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("productoBean")
@ViewScoped
public class ProductoBean implements ProductoViewModel, Serializable {
    private final ProductoInteractor productoInteractor;
    private List<Producto> listaProductos;
    private Producto productoSeleccionado;
    private String nombre;
    private String descripcion;
    private double precioVenta;
    private int stock;

    public ProductoBean() {
        this.productoInteractor = new ProductoService();
        cargarProductos();
    }

    @Override
    public void cargarProductos() {
        this.listaProductos = productoInteractor.listarTodos();
    }

    @Override
    public void guardarProducto() {
        try {
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecioVenta(precioVenta);
            producto.setStock(stock);
            producto.setEstado("activo");
            productoInteractor.registrarProducto(producto);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Éxito", "Producto registrado correctamente"));
            limpiarFormulario();
            cargarProductos();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    @Override
    public void actualizarProducto() {
        if (productoSeleccionado != null) {
            try {
                productoInteractor.actualizarProducto(productoSeleccionado);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Éxito", "Producto actualizado correctamente"));
                cargarProductos();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }
    }

    @Override
    public void eliminarProducto() {
        if (productoSeleccionado != null) {
            try {
                productoInteractor.eliminarProducto(productoSeleccionado.getIdProducto());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Éxito", "Producto eliminado correctamente"));
                cargarProductos();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }
    }

    @Override
    public void buscarProductos(String criterio) {
        this.listaProductos = productoInteractor.buscarProductos(criterio);
    }

    @Override
    public void limpiarFormulario() {
        nombre = null;
        descripcion = null;
        precioVenta = 0;
        stock = 0;
    }

    @Override
    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public void setProductoSeleccionado(Producto productoSeleccionado) { this.productoSeleccionado = productoSeleccionado; }
    @Override
    public List<Producto> getListaProductos() { return listaProductos; }
    @Override
    public String getNombre() { return nombre; }
    @Override
    public void setNombre(String nombre) { this.nombre = nombre; }
    @Override
    public String getDescripcion() { return descripcion; }
    @Override
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    @Override
    public double getPrecioVenta() { return precioVenta; }
    @Override
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    @Override
    public int getStock() { return stock; }
    @Override
    public void setStock(int stock) { this.stock = stock; }
}
