package com.example.sistemafacturacion.services;

import com.example.sistemafacturacion.data.DetalleFactura;
import com.example.sistemafacturacion.database.DetalleFacturaRepositoryImpl;
import com.example.sistemafacturacion.interfaces.interactor.DetalleFacturaInteractor;
import com.example.sistemafacturacion.interfaces.repository.DetalleFacturaRepository;
import java.util.List;

public class DetalleFacturaService implements DetalleFacturaInteractor {
    private final DetalleFacturaRepository detalleRepository;

    public DetalleFacturaService() {
        this.detalleRepository = new DetalleFacturaRepositoryImpl();
    }

    @Override
    public DetalleFactura crearDetalle(DetalleFactura detalle) {
        return detalleRepository.crear(detalle);
    }

    @Override
    public List<DetalleFactura> obtenerDetallesPorFactura(int idFactura) {
        return detalleRepository.obtenerPorFactura(idFactura);
    }
}
