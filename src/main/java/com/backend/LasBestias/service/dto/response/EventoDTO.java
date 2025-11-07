package com.backend.LasBestias.service.dto.response;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class EventoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String lugar;
    private String ciudad;
    private LocalDateTime fechaEvento;
    private Long imageId;
    private String imagenUrl;
}
