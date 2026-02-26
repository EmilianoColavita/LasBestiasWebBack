package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.repository.EntradaRepository;
import com.backend.LasBestias.service.EntradaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntradaServiceImpl implements EntradaService {

    private final EntradaRepository entradaRepository;

    public EntradaServiceImpl(EntradaRepository entradaRepository) {
        this.entradaRepository = entradaRepository;
    }

    @Override
    public void registrarEntrada(Entrada entrada) {
        entradaRepository.save(entrada);
    }

    @Override
    public boolean existePorPaymentId(String paymentId) {
        return entradaRepository.existsByPaymentId(paymentId);
    }

    @Override
    public List<Entrada> obtenerPorEvento(Long eventoId) {
        return entradaRepository.findByEventoId(eventoId);
    }

    @Override
    public List<Entrada> obtenerTodas() {
        return entradaRepository.findAll();
    }

    @Override
    public Optional<Entrada> buscarPorQrToken(String qrToken) {
        return entradaRepository.findByQrToken(qrToken);
    }

    @Override
    public List<Entrada> buscarTodasPorPaymentId(String paymentId) {
        return entradaRepository.findByPaymentId(paymentId);
    }

    @Override
    public void guardar(Entrada entrada) {
        entradaRepository.save(entrada);
    }
}