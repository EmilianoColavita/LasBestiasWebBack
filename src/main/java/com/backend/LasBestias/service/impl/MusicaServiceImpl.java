package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.MusicaService;
import com.backend.LasBestias.service.SpotifyAuthService;
import com.backend.LasBestias.service.dto.response.MusicaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicaServiceImpl implements MusicaService {

    @Value("${spotify.artist.id}")
    private String artistId;

    private final SpotifyAuthService spotifyAuthService;

    @Override
    @Cacheable("spotify-musica")
    public List<MusicaDTO> getMusica() {

        log.info("🎵 Obteniendo música desde Spotify...");

        String token = spotifyAuthService.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String albumsUrl = UriComponentsBuilder
                .fromHttpUrl("https://api.spotify.com/v1/artists/{id}/albums")
                .queryParam("limit", 20)
                .buildAndExpand(artistId)
                .toUriString();

        log.info("Spotify URL: {}", albumsUrl);

        try {

            ResponseEntity<Map> albumsResponse =
                    restTemplate.exchange(albumsUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> body = albumsResponse.getBody();

            if (body == null || !body.containsKey("items")) {
                log.error("Spotify devolvió respuesta inválida");
                return new ArrayList<>();
            }

            List<Map<String, Object>> albums =
                    (List<Map<String, Object>>) body.get("items");

            Map<String, MusicaDTO> cancionesUnicas = new LinkedHashMap<>();

            for (Map<String, Object> album : albums) {

                String albumId = (String) album.get("id");
                String albumName = (String) album.get("name");

                List<Map<String, Object>> images =
                        (List<Map<String, Object>>) album.get("images");

                String imagenUrl =
                        images != null && !images.isEmpty()
                                ? (String) images.get(0).get("url")
                                : null;

                String tracksUrl =
                        String.format("https://api.spotify.com/v1/albums/%s/tracks?limit=50", albumId);

                ResponseEntity<Map> tracksResponse =
                        restTemplate.exchange(tracksUrl, HttpMethod.GET, entity, Map.class);

                Map<String, Object> tracksBody = tracksResponse.getBody();

                if (tracksBody == null || !tracksBody.containsKey("items")) continue;

                List<Map<String, Object>> tracks =
                        (List<Map<String, Object>>) tracksBody.get("items");

                for (Map<String, Object> track : tracks) {

                    String id = (String) track.get("id");
                    if (id == null) continue;

                    if (cancionesUnicas.containsKey(id)) continue;

                    String titulo = (String) track.get("name");

                    Map<String, Object> externalUrls =
                            (Map<String, Object>) track.get("external_urls");

                    String urlSpotify =
                            externalUrls != null ? (String) externalUrls.get("spotify") : null;

                    MusicaDTO dto = new MusicaDTO(
                            id,
                            titulo,
                            albumName,
                            id,
                            urlSpotify,
                            imagenUrl
                    );

                    cancionesUnicas.put(id, dto);
                }
            }

            List<MusicaDTO> resultado = new ArrayList<>(cancionesUnicas.values());

            log.info("✅ Canciones obtenidas: {}", resultado.size());

            return resultado;

        } catch (Exception e) {

            log.error("❌ Error consultando Spotify: {}", e.getMessage());
            e.printStackTrace();

            return new ArrayList<>();
        }
    }
}