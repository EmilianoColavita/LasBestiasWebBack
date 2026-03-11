package com.backend.LasBestias.controller;

import com.backend.LasBestias.Security.RateLimitService;
import com.backend.LasBestias.service.EmailService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/contacto")
public class ContactoController {

    private final EmailService emailService;
    private final RateLimitService rateLimitService;

    public ContactoController(EmailService emailService,
                              RateLimitService rateLimitService) {
        this.emailService = emailService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping
    public ResponseEntity<?> enviarMensaje(@RequestBody Map<String, String> body,
                                           HttpServletRequest request) {

        String ip = request.getRemoteAddr();

        if (!rateLimitService.isAllowed(ip)) {
            return ResponseEntity.status(429)
                    .body(Map.of("error", "Demasiados mensajes. Intentá en 1 minuto."));
        }

        String nombre = body.get("nombre");
        String email = body.get("email");
        String mensaje = body.get("mensaje");

        emailService.enviarMensajeContacto(nombre, email, mensaje);

        return ResponseEntity.ok(Map.of("status", "mensaje enviado"));
    }
}