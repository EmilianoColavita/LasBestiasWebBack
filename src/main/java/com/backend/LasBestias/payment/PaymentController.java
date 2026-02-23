package com.backend.LasBestias.payment;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/pagos")
public class PaymentController {

    @Value("${mercadopago.access_token}")
    private String accessToken;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.backend.url}")
    private String backendUrl;

    @PostMapping("/crear-preferencia")
    public Map<String, Object> crearPreferencia(@RequestBody PaymentRequest request) {

        String url = "https://api.mercadopago.com/checkout/preferences";
        RestTemplate rest = new RestTemplate();

        Map<String, Object> item = Map.of(
                "title", "Entrada para evento " + request.getEventoId(),
                "quantity", 1,
                "unit_price", 100,
                "currency_id", "ARS"
        );

        Map<String, Object> payer = Map.of(
                "email", request.getEmail(),
                "name", request.getNombre(),
                "surname", request.getApellido()
        );

        // 🔥 EXTERNAL REFERENCE con todos los datos
        String externalRef =
                request.getEventoId() + "|" +
                        request.getEmail() + "|" +
                        request.getNombre() + "|" +
                        request.getApellido();

        Map<String, Object> backUrls = Map.of(
                "success", frontendUrl + "/pago-exitoso",
                "failure", frontendUrl + "/pago-error",
                "pending", frontendUrl + "/pago-pendiente"
        );

        Map<String, Object> preference = new HashMap<>();
        preference.put("items", List.of(item));
        preference.put("payer", payer);
        preference.put("external_reference", externalRef);

        // Webhook directo al backend
        preference.put("notification_url",
                backendUrl + "/api/pagos/webhook");

        preference.put("back_urls", backUrls);
        preference.put("auto_return", "approved");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(preference, headers);

        ResponseEntity<Map> response =
                rest.postForEntity(url, entity, Map.class);

        return response.getBody();
    }
}