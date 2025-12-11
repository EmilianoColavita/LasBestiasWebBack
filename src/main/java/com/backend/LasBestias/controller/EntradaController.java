package com.backend.LasBestias.controller;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.service.EntradaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entradas")
public class EntradaController {

    private final EntradaService entradaService;

    public EntradaController(EntradaService entradaService) {
        this.entradaService = entradaService;
    }

    // Listar TODAS las entradas
    @GetMapping
    public List<Entrada> listarTodas() {
        return entradaService.obtenerTodas();
    }

    // Listar entradas por evento
    @GetMapping("/evento/{eventoId}")
    public List<Entrada> obtenerEntradasPorEvento(@PathVariable Long eventoId) {
        return entradaService.obtenerPorEvento(eventoId);
    }
}
