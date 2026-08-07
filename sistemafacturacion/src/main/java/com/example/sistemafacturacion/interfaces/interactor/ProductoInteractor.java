package com.example.sistemafacturacion.interfaces.interactor;

import com.example.sistemafacturacion.data.Producto;
import java.util.List;

public interface ProductoInteractor {
    Producto registrarProducto(Producto producto);
    Producto obtenerPorId(int idProducto);
    List<Producto> listarTodos();
    Producto actualizarProducto(Producto producto);
    boolean eliminarProducto(int idProducto);
    List<Producto> buscarProductos(String criterio);
    boolean verificarStock(int idProducto, int cantidad);
    void reducirStock(int idProducto, int cantidad);
}
