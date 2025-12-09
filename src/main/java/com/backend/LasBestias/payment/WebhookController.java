package com.backend.LasBestias.payment;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.service.EntradaService;
import com.backend.LasBestias.service.EmailService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class WebhookController {

    private final String accessToken = System.getenv("MERCADOPAGO_ACCESS_TOKEN");

    private final EntradaService entradaService;
    private final EmailService emailService;

    public WebhookController(EntradaService entradaService, EmailService emailService) {
        this.entradaService = entradaService;
        this.emailService = emailService;
    }

    @PostMapping("/webhook")
    public void webhook(@RequestBody Map<String, Object> data) {

        if (!"payment".equals(data.get("type"))) return;

        Map<String, Object> datosPago = (Map<String, Object>) data.get("data");
        String paymentId = datosPago.get("id").toString();

        RestTemplate rest = new RestTemplate();

        String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = rest.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> mpPayment = response.getBody();

        if ("approved".equals(mpPayment.get("status"))) {

            Map metadata = (Map) mpPayment.get("metadata");

            String email = (String) metadata.get("email");
            String nombre = (String) metadata.get("nombre");
            String apellido = (String) metadata.get("apellido");
            Long eventoId = Long.valueOf(metadata.get("eventoId").toString());

            // Guardar entrada
            Entrada entrada = new Entrada();
            entrada.setEventoId(eventoId);
            entrada.setEmail(email);
            entrada.setNombre(nombre);
            entrada.setApellido(apellido);
            entrada.setPaymentId(paymentId);
            entrada.setFechaCompra(LocalDateTime.now());

            entradaService.registrarEntrada(entrada);

            // Enviar email
            String asunto = "Confirmación de compra - Las Bestias";
            String mensajeHtml =
                    "<h1>¡Gracias por tu compra!</h1>" +
                            "<p>Hola " + nombre + " " + apellido + ", tu entrada fue confirmada.</p>" +
                            "<p><strong>Evento ID:</strong> " + eventoId + "</p>" +
                            "<p><strong>Payment ID:</strong> " + paymentId + "</p>" +
                            "<p>Nos vemos pronto 🤘🔥</p>";

            emailService.enviarConfirmacion(email, asunto, mensajeHtml);

            System.out.println("✔ Entrada registrada y email enviado");
        }
    }
}
