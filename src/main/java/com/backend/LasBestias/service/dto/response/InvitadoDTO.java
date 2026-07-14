package com.backend.LasBestias.service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvitadoDTO {

    private Long id;
    private Long eventoId;
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDateTime fechaCarga;
}