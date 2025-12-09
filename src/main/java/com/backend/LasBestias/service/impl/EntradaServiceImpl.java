package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.repository.EntradaRepository;
import com.backend.LasBestias.service.EntradaService;
import org.springframework.stereotype.Service;

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
}
