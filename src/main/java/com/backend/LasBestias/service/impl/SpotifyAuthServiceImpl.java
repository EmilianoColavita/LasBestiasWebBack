package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.SpotifyAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class SpotifyAuthServiceImpl implements SpotifyAuthService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private String cachedToken;
    private long tokenExpirationTime = 0; // timestamp en ms

    @Override
    public String getAccessToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            return cachedToken;
        }

        String url = "https://accounts.spotify.com/api/token";

        RestTemplate restTemplate = new RestTemplate();

        // 1️⃣ Crear el header Authorization: Basic base64(client_id:client_secret)
        String authHeader = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + authHeader);

        // 2️⃣ Body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            // 3️⃣ Llamar a Spotify
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
                throw new RuntimeException("No se pudo obtener el token de Spotify");
            }

            String token = (String) response.getBody().get("access_token");
            int expiresIn = (Integer) response.getBody().get("expires_in");

            cachedToken = token;
            tokenExpirationTime = System.currentTimeMillis() + (expiresIn * 1000L);

            log.info("🎧 Token de Spotify obtenido correctamente. Expira en {} segundos", expiresIn);

            return token;

        } catch (Exception e) {
            log.error("❌ Error al obtener el token de Spotify: {}", e.getMessage());
            throw new RuntimeException("Error al obtener token de Spotify", e);
        }
    }
}
