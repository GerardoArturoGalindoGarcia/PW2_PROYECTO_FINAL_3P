package com.example.sistemafacturacion.interfaces.interactor;

import com.example.sistemafacturacion.data.DetalleFactura;
import java.util.List;

public interface DetalleFacturaInteractor {
    DetalleFactura crearDetalle(DetalleFactura detalle);
    List<DetalleFactura> obtenerDetallesPorFactura(int idFactura);
}
