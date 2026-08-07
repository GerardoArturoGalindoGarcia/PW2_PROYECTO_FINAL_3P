package com.example.sistemafacturacion.services;

import com.example.sistemafacturacion.data.CAI;
import com.example.sistemafacturacion.database.CAIRepositoryImpl;
import com.example.sistemafacturacion.interfaces.interactor.CAIInteractor;
import com.example.sistemafacturacion.interfaces.repository.CAIRepository;
import java.util.List;

public class CAIService implements CAIInteractor {
    private final CAIRepository caiRepository;

    public CAIService() {
        this.caiRepository = new CAIRepositoryImpl();
    }

    @Override
    public CAI registrarCAI(CAI cai) {
        return caiRepository.crear(cai);
    }

    @Override
    public CAI obtenerPorId(int idCAI) {
        return caiRepository.obtenerPorId(idCAI);
    }

    @Override
    public List<CAI> listarTodas() {
        return caiRepository.obtenerTodas();
    }

    @Override
    public CAI actualizarCAI(CAI cai) {
        return caiRepository.actualizar(cai);
    }

    @Override
    public boolean eliminarCAI(int idCAI) {
        return caiRepository.eliminar(idCAI);
    }

    @Override
    public CAI obtenerCAIActivo() {
        return caiRepository.obtenerCAIActivo();
    }

    @Override
    public int obtenerSiguienteNumeroFactura() {
        CAI cai = obtenerCAIActivo();
        if (cai == null) {
            throw new RuntimeException("No hay CAI activo");
        }
        return cai.getSiguienteFactura();
    }

    @Override
    public boolean validarRangoFactura(int numeroFactura, CAI cai) {
        return numeroFactura >= cai.getRangoInicial() && numeroFactura <= cai.getRangoFinal();
    }
}
