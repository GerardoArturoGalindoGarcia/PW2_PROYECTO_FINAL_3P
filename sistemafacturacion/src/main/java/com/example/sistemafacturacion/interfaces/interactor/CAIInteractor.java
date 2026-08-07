package com.example.sistemafacturacion.interfaces.interactor;

import com.example.sistemafacturacion.data.CAI;
import java.util.List;

public interface CAIInteractor {
    CAI registrarCAI(CAI cai);
    CAI obtenerPorId(int idCAI);
    List<CAI> listarTodas();
    CAI actualizarCAI(CAI cai);
    boolean eliminarCAI(int idCAI);
    CAI obtenerCAIActivo();
    int obtenerSiguienteNumeroFactura();
    boolean validarRangoFactura(int numeroFactura, CAI cai);
}
