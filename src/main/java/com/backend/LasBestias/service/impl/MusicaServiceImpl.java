package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.MusicaService;
import com.backend.LasBestias.service.SpotifyAuthService;
import com.backend.LasBestias.service.dto.response.MusicaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MusicaServiceImpl implements MusicaService {

    @Value("${spotify.artist.id}")
    private String artistId;

    private final SpotifyAuthService spotifyAuthService;

    @Override
    public List<MusicaDTO> getMusica() {
        String token = spotifyAuthService.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 1️⃣ Obtener todos los álbumes del artista
        String albumsUrl = String.format(
                "https://api.spotify.com/v1/artists/%s/albums?include_groups=album,single&market=AR&limit=50",
                artistId
        );
        ResponseEntity<Map> albumsResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, entity, Map.class);
        List<Map<String, Object>> albums = (List<Map<String, Object>>) albumsResponse.getBody().get("items");

        List<MusicaDTO> resultado = new ArrayList<>();

        if (albums == null) {
            throw new RuntimeException("No se pudieron obtener los álbumes del artista");
        }

        // 2️⃣ Recorrer los álbumes y traer sus canciones
        for (Map<String, Object> album : albums) {
            String albumId = (String) album.get("id");
            String albumName = (String) album.get("name");

            // Imagen del álbum
            List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
            String imagenUrl = images != null && !images.isEmpty() ? (String) images.get(0).get("url") : null;

            // Canciones del álbum
            String tracksUrl = String.format("https://api.spotify.com/v1/albums/%s/tracks", albumId);
            ResponseEntity<Map> tracksResponse = restTemplate.exchange(tracksUrl, HttpMethod.GET, entity, Map.class);
            List<Map<String, Object>> tracks = (List<Map<String, Object>>) tracksResponse.getBody().get("items");

            if (tracks == null) continue;

            for (Map<String, Object> track : tracks) {
                String id = (String) track.get("id");
                String titulo = (String) track.get("name");

                Map<String, Object> externalUrls = (Map<String, Object>) track.get("external_urls");
                String urlSpotify = externalUrls != null ? (String) externalUrls.get("spotify") : null;

                resultado.add(new MusicaDTO(id, titulo, albumName, id, urlSpotify, imagenUrl));
            }
        }

        return resultado;
    }
}
