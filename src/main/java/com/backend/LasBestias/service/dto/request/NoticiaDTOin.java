package com.backend.LasBestias.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticiaDTOin {
    private String titulo;
    private String descripcion;
    private MultipartFile image;
}
