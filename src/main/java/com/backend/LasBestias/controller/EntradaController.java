package com.backend.LasBestias.controller;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.service.EntradaService;
import com.backend.LasBestias.service.EventoService;
import com.backend.LasBestias.service.QRService;
import com.backend.LasBestias.service.TicketPDFService;
import com.backend.LasBestias.service.dto.response.EventoDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entradas")
public class EntradaController {

    private final EntradaService entradaService;
    private final QRService qrService;
    private final TicketPDFService ticketPDFService;
    private final EventoService eventoService;

    public EntradaController(EntradaService entradaService,
                             QRService qrService,
                             TicketPDFService ticketPDFService,
                             EventoService eventoService) {
        this.entradaService = entradaService;
        this.qrService = qrService;
        this.ticketPDFService = ticketPDFService;
        this.eventoService = eventoService;
    }

    // 🔹 Listar todas
    @GetMapping
    public List<Entrada> listarTodas() {
        return entradaService.obtenerTodas();
    }

    // 🔹 Listar por evento
    @GetMapping("/evento/{eventoId}")
    public List<Entrada> obtenerEntradasPorEvento(@PathVariable Long eventoId) {
        return entradaService.obtenerPorEvento(eventoId);
    }

    // 🔹 Validar QR
    @PostMapping("/validar")
    public ResponseEntity<?> validarEntrada(@RequestBody Map<String, String> body) {

        String token = body.get("token");

        Optional<Entrada> optionalEntrada =
                entradaService.buscarPorQrToken(token);

        if (optionalEntrada.isEmpty()) {
            return ResponseEntity.badRequest().body("ENTRADA_INVALIDA");
        }

        Entrada entrada = optionalEntrada.get();

        if (Boolean.TRUE.equals(entrada.getUsada())) {
            return ResponseEntity.badRequest().body("ENTRADA_YA_USADA");
        }

        entrada.setUsada(true);
        entrada.setFechaUso(LocalDateTime.now());
        entradaService.guardar(entrada);

        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "comprador", entrada.getNombreComprador(),
                "eventoId", entrada.getEventoId()
        ));
    }

    // 🔹 RESUMEN PARA FRONTEND
    @GetMapping("/payment/{paymentId}/resumen")
    public ResponseEntity<?> obtenerResumen(@PathVariable String paymentId) {

        List<Entrada> entradas =
                entradaService.buscarTodasPorPaymentId(paymentId);

        if (entradas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Entrada primera = entradas.get(0);

        EventoDTO evento = eventoService.getById(primera.getEventoId());

        return ResponseEntity.ok(Map.of(
                "evento", evento.getNombre(), // ajustá si el DTO usa otro nombre
                "cantidad", entradas.size(),
                "orden", paymentId,
                "pdfDisponible", true
        ));
    }

    // 🔹 DESCARGAR PDF CON TODAS LAS ENTRADAS
    @GetMapping("/payment/{paymentId}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable String paymentId) {

        List<Entrada> entradas =
                entradaService.buscarTodasPorPaymentId(paymentId);

        if (entradas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Entrada primera = entradas.get(0);

        EventoDTO evento =
                eventoService.getById(primera.getEventoId());

        List<String> codigosQR = entradas.stream()
                .map(Entrada::getQrToken)
                .collect(Collectors.toList());

        byte[] pdf = ticketPDFService.generarPDF(
                primera.getNombreComprador(),
                evento.getNombre(), // ajustá si es getTitulo()
                entradas.size(),
                codigosQR
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=entradas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // 🔹 Descargar QR individual
    @GetMapping("/qr/{token}")
    public ResponseEntity<byte[]> descargarQR(@PathVariable String token) {

        Optional<Entrada> entrada =
                entradaService.buscarPorQrToken(token);

        if (entrada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        byte[] qrImage = qrService.generarQR(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=entrada.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }
}