package com.backend.LasBestias.controller;

import com.backend.LasBestias.service.MusicaService;
import com.backend.LasBestias.service.dto.response.MusicaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/musica")
@RequiredArgsConstructor
public class MusicaController {

    private final MusicaService musicaService;

    @GetMapping
    public List<MusicaDTO> getMusica() {
        return musicaService.getMusica();
    }
}
