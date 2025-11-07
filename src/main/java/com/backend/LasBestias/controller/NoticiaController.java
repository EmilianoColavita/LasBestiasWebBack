package com.backend.LasBestias.controller;

import com.backend.LasBestias.util.PaginationUtil;
import com.backend.LasBestias.service.NoticiaService;
import com.backend.LasBestias.service.dto.request.NoticiaDTOin;
import com.backend.LasBestias.service.dto.response.NoticiaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/noticias")
@Tag(name = "Noticia", description = "Endpoints para gestión de noticias")
public class NoticiaController {

    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear una noticia")
    public ResponseEntity<NoticiaDTO> create(@RequestParam String titulo,
                                             @RequestParam String descripcion,
                                             @RequestParam(required = false) MultipartFile image) {
        NoticiaDTOin dto = new NoticiaDTOin(titulo, descripcion, image);
        NoticiaDTO response = noticiaService.create(dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @Operation(summary = "Obtener todas las noticias")
    public ResponseEntity<List<NoticiaDTO>> getAll(@ParameterObject Pageable pageable) {
        Page<NoticiaDTO> response = noticiaService.getAll(pageable);
        HttpHeaders headers = PaginationUtil.setTotalCountPageHttpHeaders(response);
        return new ResponseEntity<>(response.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una noticia por ID")
    public ResponseEntity<NoticiaDTO> getById(@PathVariable Long id) {
        NoticiaDTO response = noticiaService.getById(id);
        return ResponseEntity.ok(response);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar una noticia")
    public ResponseEntity<NoticiaDTO> update(@PathVariable Long id,
                                             @RequestParam String titulo,
                                             @RequestParam String descripcion,
                                             @RequestParam(required = false) MultipartFile image) {
        NoticiaDTOin dto = new NoticiaDTOin(titulo, descripcion, image);
        NoticiaDTO response = noticiaService.update(id, dto);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una noticia")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticiaService.delete(id);
        return ResponseEntity.ok().build();
    }
}
