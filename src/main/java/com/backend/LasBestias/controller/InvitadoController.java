package com.backend.LasBestias.controller;

import com.backend.LasBestias.service.InvitadoService;
import com.backend.LasBestias.service.dto.request.InvitadoDTOin;
import com.backend.LasBestias.service.dto.response.InvitadoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitados")
@Tag(name = "Invitados", description = "Gestión de lista de invitados por evento")
public class InvitadoController {

    private final InvitadoService invitadoService;

    public InvitadoController(InvitadoService invitadoService) {
        this.invitadoService = invitadoService;
    }

    @PostMapping
    @Operation(summary = "Agregar un invitado a un evento")
    public ResponseEntity<InvitadoDTO> crear(@RequestBody InvitadoDTOin dto) {
        return ResponseEntity.ok(invitadoService.crear(dto));
    }

    @GetMapping("/evento/{eventoId}")
    @Operation(summary = "Listar invitados de un evento")
    public ResponseEntity<List<InvitadoDTO>> listarPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(invitadoService.listarPorEvento(eventoId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un invitado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        invitadoService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validar")
    @Operation(summary = "Validar si un DNI está en la lista de invitados de un evento")
    public ResponseEntity<Map<String, Object>> validarDni(
            @RequestParam Long eventoId,
            @RequestParam String dni) {
        return ResponseEntity.ok(invitadoService.validarDni(eventoId, dni));
    }
}
