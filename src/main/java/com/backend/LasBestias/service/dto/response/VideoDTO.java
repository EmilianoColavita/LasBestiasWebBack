package com.backend.LasBestias.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private String videoId;
    private String titulo;
    private String descripcion;
    private String thumbnailUrl;
}
