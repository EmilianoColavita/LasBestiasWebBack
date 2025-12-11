package com.backend.LasBestias.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/pagos")
public class PaymentController {

    private final String accessToken = System.getenv("MERCADOPAGO_ACCESS_TOKEN");

    @PostMapping("/crear-preferencia")
    public Map<String, Object> crearPreferencia(@RequestBody PaymentRequest request) {

        String url = "https://api.mercadopago.com/checkout/preferences";
        RestTemplate rest = new RestTemplate();

        // ÍTEM
        Map<String, Object> item = Map.of(
                "title", "Entrada para evento " + request.getEventoId(),
                "quantity", 1,
                "unit_price", 100,
                "currency_id", "ARS"
        );

        // PAGADOR
        Map<String, Object> payer = Map.of(
                "email", request.getEmail(),
                "name", request.getNombre(),
                "surname", request.getApellido()
        );

        // METADATA
        Map<String, Object> metadata = Map.of(
                "eventoId", request.getEventoId(),
                "email", request.getEmail(),
                "nombre", request.getNombre(),
                "apellido", request.getApellido()
        );

        // EXTERNAL REFERENCE
        String externalRef = UUID.randomUUID().toString();

        // PREFERENCE COMPLETA
        Map<String, Object> preference = new HashMap<>();
        preference.put("items", List.of(item));
        preference.put("payer", payer);
        preference.put("metadata", metadata);
        preference.put("external_reference", externalRef);
        preference.put("notification_url",
                "https://lasbestiaswebback-is4p.onrender.com/api/pagos/webhook");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(preference, headers);

        ResponseEntity<Map> response = rest.postForEntity(url, entity, Map.class);

        return response.getBody();
    }
}

