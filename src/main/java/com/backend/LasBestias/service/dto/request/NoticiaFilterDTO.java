package com.backend.LasBestias.service.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticiaFilterDTO {
    private String titulo;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
}