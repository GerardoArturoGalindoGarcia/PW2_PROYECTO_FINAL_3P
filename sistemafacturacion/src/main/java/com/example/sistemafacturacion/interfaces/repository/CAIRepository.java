package com.example.sistemafacturacion.interfaces.repository;

import com.example.sistemafacturacion.data.CAI;
import java.util.List;

public interface CAIRepository {
    CAI crear(CAI cai);
    CAI obtenerPorId(int idCAI);
    CAI obtenerPorCodigoCAI(String codigoCAI);
    List<CAI> obtenerTodas();
    CAI actualizar(CAI cai);
    boolean eliminar(int idCAI);
    CAI obtenerCAIActivo();
    void actualizarSiguienteFactura(int idCAI, int siguiente);
}
