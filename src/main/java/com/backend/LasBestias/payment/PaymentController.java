package com.backend.LasBestias.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/pagos")
public class PaymentController {

    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accessToken;

    @PostMapping("/crear-preferencia")
    public Map<String, Object> crearPreferencia(@RequestBody PaymentRequest request) {

        String url = "https://api.mercadopago.com/checkout/preferences";
        RestTemplate rest = new RestTemplate();

        // ITEM
        Map<String, Object> item = new HashMap<>();
        item.put("title", "Entrada para evento " + request.getEventoId());
        item.put("quantity", 1);
        item.put("unit_price", 100);
        item.put("currency_id", "ARS");

        // PAYER
        Map<String, Object> payer = new HashMap<>();
        payer.put("email", request.getEmail());
        payer.put("name", request.getNombre());
        payer.put("surname", request.getApellido());

        // METADATA
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("eventoId", request.getEventoId());
        metadata.put("email", request.getEmail());
        metadata.put("nombre", request.getNombre());
        metadata.put("apellido", request.getApellido());

        // PREFERENCE
        Map<String, Object> preference = new HashMap<>();
        preference.put("items", List.of(item));
        preference.put("payer", payer);
        preference.put("metadata", metadata);

        // ⚠ IMPORTANTE: si trabajás local, usar esto:
        preference.put("notification_url", "https://webhook.site/e607d295-0755-4b03-ab3f-7950f685bd61");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(preference, headers);

        ResponseEntity<Map> response = rest.postForEntity(url, entity, Map.class);

        return response.getBody();
    }
}
