package com.backend.LasBestias.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicaDTO {
    private String id;
    private String titulo;
    private String tipo;
    private String spotifyId;
    private String urlSpotify;
    private String imagenUrl;
}
