package com.backend.LasBestias.service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticiaDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaPublicacion;
    private Long imageId;
    private String imagenUrl;
}
