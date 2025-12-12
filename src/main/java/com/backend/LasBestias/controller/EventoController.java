package com.backend.LasBestias.controller;

import com.backend.LasBestias.util.PaginationUtil;
import com.backend.LasBestias.service.EventoService;
import com.backend.LasBestias.service.dto.request.EventoDTOin;
import com.backend.LasBestias.service.dto.response.EventoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Evento", description = "Endpoints para gestión de eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // 🟢 Crear un evento
    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Crear un evento")
    public ResponseEntity<EventoDTO> create(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String lugar,
            @RequestParam String ciudad,
            @RequestParam String fechaEvento, // formato esperado: "2025-11-10"
            @RequestParam(required = false) MultipartFile image
    ) {
        LocalDateTime fecha = LocalDateTime.parse(fechaEvento);

        EventoDTOin dto = new EventoDTOin(
                nombre,
                descripcion,
                lugar,
                ciudad,
                fecha,
                image
        );
        return ResponseEntity.ok(eventoService.create(dto));
    }

    // 🟡 Obtener todos los eventos
    @GetMapping
    @Operation(summary = "Obtener todos los eventos")
    public ResponseEntity<List<EventoDTO>> getAll(
            @ParameterObject
            @PageableDefault(sort = "fechaEvento", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<EventoDTO> response = eventoService.getAll(pageable);
        HttpHeaders headers = PaginationUtil.setTotalCountPageHttpHeaders(response);
        return new ResponseEntity<>(response.getContent(), headers, HttpStatus.OK);
    }

    // 🔵 Obtener evento por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener evento por ID")
    public ResponseEntity<EventoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.getById(id));
    }

    // 🟢 Obtener solo eventos futuros
    @GetMapping("/lista/futuros")
    @Operation(summary = "Obtener solo eventos futuros")
    public ResponseEntity<List<EventoDTO>> getEventosFuturos() {
        return ResponseEntity.ok(eventoService.getEventosFuturos());
    }


    // 🟣 Actualizar un evento
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @Operation(summary = "Actualizar un evento")
    public ResponseEntity<EventoDTO> update(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String lugar,
            @RequestParam String ciudad,
            @RequestParam String fechaEvento,
            @RequestParam(required = false) MultipartFile image
    ) {
        LocalDateTime fecha = LocalDateTime.parse(fechaEvento); // ✅ igual que arriba

        EventoDTOin dto = new EventoDTOin(
                nombre,
                descripcion,
                lugar,
                ciudad,
                fecha,
                image
        );
        return ResponseEntity.ok(eventoService.update(id, dto));
    }

    // 🔴 Eliminar un evento
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventoService.delete(id);
        return ResponseEntity.ok().build();
    }
}
