package com.backend.LasBestias.service.dto.request;

import lombok.Data;


import java.time.LocalDateTime;


@Data
public class EventoFilterDTO {
    private String nombre;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
}
