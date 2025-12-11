package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.repository.EntradaRepository;
import com.backend.LasBestias.service.EntradaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntradaServiceImpl implements EntradaService {

    private final EntradaRepository entradaRepository;

    public EntradaServiceImpl(EntradaRepository entradaRepository) {
        this.entradaRepository = entradaRepository;
    }

    @Override
    public void registrarEntrada(Entrada entrada) {

        if (entradaRepository.existsByPaymentId(entrada.getPaymentId())) {
            System.out.println("⚠ Entrada ya existe para paymentId=" + entrada.getPaymentId());
            return;
        }

        entradaRepository.save(entrada);
    }


    @Override
    public List<Entrada> obtenerTodas() {
        return entradaRepository.findAll();
    }


    @Override
    public boolean existePorPaymentId(String paymentId) {
        return entradaRepository.existsByPaymentId(paymentId);
    }

    @Override
    public List<Entrada> obtenerPorEvento(Long eventoId) {
        return entradaRepository.findByEventoId(eventoId);
    }
}
