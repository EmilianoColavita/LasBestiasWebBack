package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.VideoService;
import com.backend.LasBestias.service.dto.response.VideoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.channel.id}")
    private String channelId;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public VideoDTO getUltimoVideo() {

        List<VideoDTO> videos = fetchUploads(1);
        return videos.isEmpty() ? null : videos.get(0);
    }

    @Override
    public List<VideoDTO> getVideosRecientes() {

        return fetchUploads(4);
    }

    /**
     * Lee la playlist de "uploads" del canal (UU...) en lugar de usar /search.
     * /search devuelve también playlists (y cuesta 100 unidades de cuota por llamada);
     * playlistItems devuelve solo videos subidos y cuesta 1 unidad.
     */
    private List<VideoDTO> fetchUploads(int maxResults) {

        String uploadsPlaylistId = channelId.startsWith("UC")
                ? "UU" + channelId.substring(2)
                : channelId;

        String url = String.format(
                "https://www.googleapis.com/youtube/v3/playlistItems?key=%s&playlistId=%s&part=snippet&maxResults=%d",
                apiKey, uploadsPlaylistId, maxResults
        );

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map body = response.getBody();

        if (body == null || !body.containsKey("items")) return List.of();

        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

        return items.stream()
                .map(this::mapToVideoDTO)
                .filter(v -> v != null)
                .toList();
    }

    private VideoDTO mapToVideoDTO(Map<String, Object> item) {

        Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
        if (snippet == null) return null;

        Map<String, Object> resourceId = (Map<String, Object>) snippet.get("resourceId");
        if (resourceId == null || !resourceId.containsKey("videoId")) {
            return null;
        }

        String videoId = (String) resourceId.get("videoId");

        // 🔥 SOLUCIÓN: des-escapamos HTML que viene de YouTube
        String rawTitle = (String) snippet.get("title");
        String title = rawTitle != null ? HtmlUtils.htmlUnescape(rawTitle) : "";

        String rawDescription = (String) snippet.get("description");
        String description = rawDescription != null
                ? HtmlUtils.htmlUnescape(rawDescription)
                : "";

        String thumbnailUrl = "";

        Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
        if (thumbnails != null && thumbnails.containsKey("high")) {
            Map<String, Object> highThumb = (Map<String, Object>) thumbnails.get("high");
            if (highThumb != null && highThumb.containsKey("url")) {
                thumbnailUrl = (String) highThumb.get("url");
            }
        }

        return new VideoDTO(videoId, title, description, thumbnailUrl);
    }
}