package com.backend.LasBestias.service.dto.response;

import com.backend.LasBestias.model.ModoVenta;
import com.backend.LasBestias.model.PlataformaVenta;
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
    private Double precio;
    private Long imageId;
    private String imagenUrl;
    private ModoVenta modoVenta;
    private PlataformaVenta plataformaVenta;
    private String urlVenta;
    private String notaVenta;
}
