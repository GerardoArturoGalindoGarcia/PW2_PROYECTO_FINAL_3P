package com.example.sistemafacturacion.interfaces.viewmodel;

import com.example.sistemafacturacion.data.Producto;
import java.util.List;

public interface ProductoViewModel {
    void cargarProductos();
    void guardarProducto();
    void actualizarProducto();
    void eliminarProducto();
    void buscarProductos(String criterio);
    void limpiarFormulario();
    Producto getProductoSeleccionado();
    List<Producto> getListaProductos();
    String getNombre();
    void setNombre(String nombre);
    String getDescripcion();
    void setDescripcion(String descripcion);
    double getPrecioVenta();
    void setPrecioVenta(double precioVenta);
    int getStock();
    void setStock(int stock);
}
