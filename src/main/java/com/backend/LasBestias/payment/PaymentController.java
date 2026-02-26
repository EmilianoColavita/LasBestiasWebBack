package com.backend.LasBestias.payment;

import com.backend.LasBestias.service.EventoService;
import com.backend.LasBestias.service.dto.response.EventoDTO;
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

    private final EventoService eventoService;

    public PaymentController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("/crear-preferencia")
    public Map<String, Object> crearPreferencia(@RequestBody PaymentRequest request) {

        RestTemplate rest = new RestTemplate();

        // 🔥 Buscar evento real desde BD
        EventoDTO evento = eventoService.getById(request.getEventoId());

        if (evento == null) {
            throw new RuntimeException("Evento no encontrado");
        }

        Double precioUnitario = evento.getPrecio();
        Integer cantidad = request.getCantidad();

        Map<String, Object> item = new HashMap<>();
        item.put("title", "Entrada - " + evento.getNombre());
        item.put("quantity", cantidad);
        item.put("unit_price", precioUnitario);
        item.put("currency_id", "ARS");

        Map<String, Object> payer = new HashMap<>();
        payer.put("email", request.getEmail());
        payer.put("name", request.getNombre());

        String externalRef =
                request.getEventoId() + "|" +
                        request.getEmail() + "|" +
                        request.getNombre() + "|" +
                        request.getTelefono() + "|" +
                        request.getDni() + "|" +
                        request.getCantidad();

        Map<String, Object> backUrls = new HashMap<>();
        backUrls.put("success", frontendUrl + "/pago-exitoso");
        backUrls.put("failure", frontendUrl + "/pago-error");
        backUrls.put("pending", frontendUrl + "/pago-pendiente");

        Map<String, Object> preference = new HashMap<>();
        preference.put("items", List.of(item));
        preference.put("payer", payer);
        preference.put("external_reference", externalRef);
        preference.put("notification_url", backendUrl + "/api/pagos/webhook");
        preference.put("back_urls", backUrls);
        preference.put("auto_return", "approved");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(preference, headers);

        ResponseEntity<Map> response =
                rest.postForEntity(
                        "https://api.mercadopago.com/checkout/preferences",
                        entity,
                        Map.class
                );

        return response.getBody();
    }
}