package com.backend.LasBestias.service;
import java.util.List;

import com.backend.LasBestias.service.dto.response.VideoDTO;

public interface VideoService {
    VideoDTO getUltimoVideo();
    List<VideoDTO> getVideosRecientes();
}
