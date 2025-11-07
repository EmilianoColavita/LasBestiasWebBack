package com.backend.LasBestias.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTOin {
    private String nombre;
    private String descripcion;
    private String lugar;
    private String ciudad;
    private LocalDateTime fechaEvento;
    private MultipartFile image;
}
