package com.backend.LasBestias.service;


import com.backend.LasBestias.service.dto.response.AlbumDTO;
import com.backend.LasBestias.service.dto.response.TrackDTO;
import java.util.List;

public interface MusicaService {

    List<TrackDTO> getMusica();

    List<AlbumDTO> getDiscografia();

}