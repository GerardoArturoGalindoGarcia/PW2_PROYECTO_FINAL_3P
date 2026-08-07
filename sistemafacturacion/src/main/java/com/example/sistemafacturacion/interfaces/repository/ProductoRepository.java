package com.example.sistemafacturacion.interfaces.repository;

import com.example.sistemafacturacion.data.Producto;
import java.util.List;

public interface ProductoRepository {
    Producto crear(Producto producto);
    Producto obtenerPorId(int idProducto);
    List<Producto> obtenerTodos();
    Producto actualizar(Producto producto);
    boolean eliminar(int idProducto);
    List<Producto> buscar(String criterio);
    void actualizarStock(int idProducto, int cantidad);
}
