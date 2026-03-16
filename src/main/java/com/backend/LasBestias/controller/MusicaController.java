package com.backend.LasBestias.controller;

import com.backend.LasBestias.service.dto.response.AlbumDTO;
import com.backend.LasBestias.service.dto.response.TrackDTO;
import com.backend.LasBestias.service.MusicaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/musica")
@CrossOrigin
public class MusicaController {

    @Autowired
    private MusicaService musicaService;

    @GetMapping
    public List<TrackDTO> getMusica() {
        return musicaService.getMusica();
    }

    @GetMapping("/discografia")
    public List<AlbumDTO> getDiscografia() {
        return musicaService.getDiscografia();
    }
}