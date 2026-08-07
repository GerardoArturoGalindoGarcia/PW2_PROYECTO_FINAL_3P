package com.example.sistemafacturacion.services;

import com.example.sistemafacturacion.data.Producto;
import com.example.sistemafacturacion.database.ProductoRepositoryImpl;
import com.example.sistemafacturacion.interfaces.interactor.ProductoInteractor;
import com.example.sistemafacturacion.interfaces.repository.ProductoRepository;
import java.util.List;

public class ProductoService implements ProductoInteractor {
    private final ProductoRepository productoRepository;

    public ProductoService() {
        this.productoRepository = new ProductoRepositoryImpl();
    }

    @Override
    public Producto registrarProducto(Producto producto) {
        return productoRepository.crear(producto);
    }

    @Override
    public Producto obtenerPorId(int idProducto) {
        return productoRepository.obtenerPorId(idProducto);
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.obtenerTodos();
    }

    @Override
    public Producto actualizarProducto(Producto producto) {
        return productoRepository.actualizar(producto);
    }

    @Override
    public boolean eliminarProducto(int idProducto) {
        return productoRepository.eliminar(idProducto);
    }

    @Override
    public List<Producto> buscarProductos(String criterio) {
        return productoRepository.buscar(criterio);
    }

    @Override
    public boolean verificarStock(int idProducto, int cantidad) {
        Producto producto = productoRepository.obtenerPorId(idProducto);
        return producto != null && producto.getStock() >= cantidad;
    }

    @Override
    public void reducirStock(int idProducto, int cantidad) {
        if (verificarStock(idProducto, cantidad)) {
            productoRepository.actualizarStock(idProducto, cantidad);
        }
    }
}
