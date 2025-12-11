package com.backend.LasBestias.service;

import com.backend.LasBestias.model.Entrada;

import java.util.List;

public interface EntradaService {
    void registrarEntrada(Entrada entrada);

    boolean existePorPaymentId(String paymentId);

    List<Entrada> obtenerPorEvento(Long eventoId);

    List<Entrada> obtenerTodas();

}
