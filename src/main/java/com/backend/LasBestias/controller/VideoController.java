package com.backend.LasBestias.controller;

import com.backend.LasBestias.service.VideoService;
import com.backend.LasBestias.service.dto.response.VideoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping("/ultimo")
    public VideoDTO getUltimoVideo() {
        return videoService.getUltimoVideo();
    }

    @GetMapping("/recientes")
    public List<VideoDTO> getVideosRecientes() {
        return videoService.getVideosRecientes();
    }

}
