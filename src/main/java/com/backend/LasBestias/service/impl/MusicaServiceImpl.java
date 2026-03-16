package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.MusicaService;
import com.backend.LasBestias.service.SpotifyAuthService;
import com.backend.LasBestias.service.dto.response.AlbumDTO;
import com.backend.LasBestias.service.dto.response.TrackDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MusicaServiceImpl implements MusicaService {

    @Value("${spotify.artist.id}")
    private String artistId;

    @Autowired
    private SpotifyAuthService spotifyAuthService;

    @Override
    @Cacheable("spotify-musica")
    public List<TrackDTO> getMusica() {

        List<TrackDTO> tracks = new ArrayList<>();

        try {

            String token = spotifyAuthService.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();

            String url =
                    "https://api.spotify.com/v1/artists/" + artistId +
                            "/albums?market=AR&limit=10&include_groups=album,single";

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            List<Map<String, Object>> items =
                    (List<Map<String, Object>>) response.getBody().get("items");

            for (Map<String, Object> album : items) {

                String albumId = (String) album.get("id");
                String albumName = (String) album.get("name");

                List<Map> images = (List<Map>) album.get("images");

                String albumImage =
                        images.isEmpty() ? null : (String) images.get(0).get("url");

                List<TrackDTO> albumTracks =
                        getTracks(albumId, token, albumImage, albumName);

                tracks.addAll(albumTracks);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tracks;
    }

    @Override
    @Cacheable("spotify-musica")
    public List<AlbumDTO> getDiscografia() {

        List<AlbumDTO> albums = new ArrayList<>();

        try {

            String token = spotifyAuthService.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();

            String url =
                    "https://api.spotify.com/v1/artists/" + artistId +
                            "/albums?market=AR&limit=50&include_groups=album,single";

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            List<Map<String, Object>> items =
                    (List<Map<String, Object>>) response.getBody().get("items");

            for (Map<String, Object> album : items) {

                String albumId = (String) album.get("id");
                String nombre = (String) album.get("name");
                String fecha = (String) album.get("release_date");

                List<Map> images = (List<Map>) album.get("images");

                String imagen =
                        images.isEmpty() ? null : (String) images.get(0).get("url");

                List<TrackDTO> tracks =
                        getTracks(albumId, token, imagen, nombre);

                AlbumDTO dto = new AlbumDTO(
                        albumId,
                        nombre,
                        imagen,
                        fecha,
                        tracks
                );

                albums.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return albums;
    }

    private List<TrackDTO> getTracks(String albumId, String token, String albumImage, String albumName) {

        List<TrackDTO> tracks = new ArrayList<>();

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();

            String url =
                    "https://api.spotify.com/v1/albums/" + albumId +
                            "/tracks?market=AR&limit=50";

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            List<Map<String, Object>> items =
                    (List<Map<String, Object>>) response.getBody().get("items");

            for (Map<String, Object> track : items) {

                String id = (String) track.get("id");
                String name = (String) track.get("name");

                TrackDTO dto = new TrackDTO(
                        id,
                        name,
                        albumImage,
                        id,
                        "https://open.spotify.com/track/" + id,
                        albumName
                );

                tracks.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tracks;
    }
}