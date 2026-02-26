package com.backend.LasBestias.service;

import com.backend.LasBestias.model.Entrada;

import java.util.List;
import java.util.Optional;

public interface EntradaService {

    void registrarEntrada(Entrada entrada);

    boolean existePorPaymentId(String paymentId);

    List<Entrada> obtenerPorEvento(Long eventoId);

    List<Entrada> obtenerTodas();

    Optional<Entrada> buscarPorQrToken(String qrToken);

    List<Entrada> buscarTodasPorPaymentId(String paymentId);

    void guardar(Entrada entrada);
}