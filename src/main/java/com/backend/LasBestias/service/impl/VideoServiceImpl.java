package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.VideoService;
import com.backend.LasBestias.service.dto.response.VideoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.channel.id}")
    private String channelId;

    @Override
    public VideoDTO getUltimoVideo() {
        String url = String.format(
                "https://www.googleapis.com/youtube/v3/search?key=%s&channelId=%s&part=snippet,id&order=date&maxResults=1",
                apiKey, channelId
        );

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        Map body = response.getBody();
        if (body == null || !body.containsKey("items")) {
            return null;
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        if (items.isEmpty()) return null;

        Map<String, Object> item = items.get(0);
        Map<String, Object> idData = (Map<String, Object>) item.get("id");
        Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");

        String videoId = (String) idData.get("videoId");
        String title = (String) snippet.get("title");
        String description = (String) snippet.get("description");

        Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
        Map<String, Object> highThumb = (Map<String, Object>) thumbnails.get("high");
        String thumbnailUrl = (String) highThumb.get("url");

        return new VideoDTO(videoId, title, description, thumbnailUrl);
    }

    @Override
    public List<VideoDTO> getVideosRecientes() {
        String url = String.format(
                "https://www.googleapis.com/youtube/v3/search?key=%s&channelId=%s&part=snippet,id&order=date&maxResults=4",
                apiKey, channelId
        );

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map body = response.getBody();
        if (body == null || !body.containsKey("items")) return List.of();

        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

        return items.stream()
                .map(item -> {
                    Map<String, Object> idData = (Map<String, Object>) item.get("id");
                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");

                    if (idData == null || !idData.containsKey("videoId")) return null;

                    String videoId = (String) idData.get("videoId");
                    String title = (String) snippet.get("title");
                    String description = (String) snippet.get("description");
                    Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                    Map<String, Object> highThumb = (Map<String, Object>) thumbnails.get("high");
                    String thumbnailUrl = (String) highThumb.get("url");

                    return new VideoDTO(videoId, title, description, thumbnailUrl);
                })
                .filter(v -> v != null)
                .toList();
    }

}
