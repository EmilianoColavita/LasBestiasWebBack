package com.backend.LasBestias.controller;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.service.EntradaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @PostMapping("/validar")
    public ResponseEntity<?> validarEntrada(@RequestBody Map<String, String> body) {

        String token = body.get("token");

        Optional<Entrada> optionalEntrada = entradaService.buscarPorQrToken(token);

        if (optionalEntrada.isEmpty()) {
            return ResponseEntity.badRequest().body("ENTRADA_INVALIDA");
        }

        Entrada entrada = optionalEntrada.get();

        if (entrada.isUsado()) {
            return ResponseEntity.badRequest().body("ENTRADA_YA_USADA");
        }

        entrada.setUsado(true);
        entrada.setFechaUso(LocalDateTime.now());

        entradaService.guardar(entrada);

        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "nombre", entrada.getNombre(),
                "apellido", entrada.getApellido(),
                "eventoId", entrada.getEventoId()
        ));
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<?> obtenerPorPayment(@PathVariable String paymentId) {

        Optional<Entrada> entrada = entradaService.buscarPorPaymentId(paymentId);

        if (entrada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(entrada.get());
    }
}


