package com.backend.LasBestias.service.dto.request;

import com.backend.LasBestias.model.ModoVenta;
import com.backend.LasBestias.model.PlataformaVenta;
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
    private Double precio;
    private MultipartFile image;

    // Datos de venta (opcionales en el request; ver reglas en EventoServiceImpl)
    private ModoVenta modoVenta;
    private PlataformaVenta plataformaVenta;
    private String urlVenta;
    private String notaVenta;
}
