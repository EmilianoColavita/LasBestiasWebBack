package com.backend.LasBestias.payment;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.service.EntradaService;
import com.backend.LasBestias.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@Slf4j
public class WebhookController {

    @Value("${mercadopago.access_token}")
    private String accessToken;

    private final EntradaService entradaService;
    private final EmailService emailService;

    public WebhookController(EntradaService entradaService,
                             EmailService emailService) {
        this.entradaService = entradaService;
        this.emailService = emailService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String id,
            @RequestBody(required = false) Map<String, Object> body) {

        log.info("🔔 Webhook recibido - topic: {}, type: {}, id: {}, body: {}",
                topic, type, id, body);

        try {

            String paymentId = null;

            // Caso 1 → viene como query param
            if ("payment".equals(topic) || "payment".equals(type)) {
                paymentId = id;
            }

            // Caso 2 → viene como JSON
            if (body != null && body.containsKey("data")) {
                Map<String, Object> data =
                        (Map<String, Object>) body.get("data");

                if (data.get("id") != null) {
                    paymentId = data.get("id").toString();
                }
            }

            if (paymentId == null) {
                log.warn("⚠ No se encontró paymentId");
                return ResponseEntity.ok("NO_PAYMENT_ID");
            }

            log.info("✔ Payment ID detectado: {}", paymentId);

            if (entradaService.existePorPaymentId(paymentId)) {
                log.warn("⚠ Pago ya procesado ({})", paymentId);
                return ResponseEntity.ok("DUPLICATE");
            }

            // 🔥 Consultar pago real en Mercado Pago
            RestTemplate rest = new RestTemplate();
            String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response =
                    rest.exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, Object> mpPayment = response.getBody();

            if (mpPayment == null) {
                log.error("❌ No llegó info del pago desde MP");
                return ResponseEntity.ok("NO_MP_DATA");
            }

            String status = (String) mpPayment.get("status");

            if (!"approved".equals(status)) {
                log.info("⏳ Pago no aprobado aún ({})", status);
                return ResponseEntity.ok("NOT_APPROVED");
            }

            Map<String, Object> metadata =
                    (Map<String, Object>) mpPayment.get("metadata");

            if (metadata == null) {
                log.error("❌ Pago aprobado sin metadata");
                return ResponseEntity.ok("NO_METADATA");
            }

            Long eventoId = Long.valueOf(metadata.get("eventoId").toString());
            String email = metadata.get("email").toString();
            String nombre = metadata.get("nombre").toString();
            String apellido = metadata.get("apellido").toString();

            Entrada entrada = new Entrada();
            entrada.setEventoId(eventoId);
            entrada.setEmail(email);
            entrada.setNombre(nombre);
            entrada.setApellido(apellido);
            entrada.setPaymentId(paymentId);
            entrada.setFechaCompra(LocalDateTime.now());

            entradaService.registrarEntrada(entrada);
            log.info("🎟 Entrada registrada correctamente en DB");

            // 🔥 Envío de mail con control de error independiente
            try {

                String asunto = "Confirmación de compra - Las Bestias";

                String mensajeHtml =
                        "<h1>¡Gracias por tu compra!</h1>" +
                                "<p>Hola " + nombre + " " + apellido + ",</p>" +
                                "<p>Tu entrada fue confirmada correctamente.</p>" +
                                "<p><strong>Evento ID:</strong> " + eventoId + "</p>" +
                                "<p><strong>Payment ID:</strong> " + paymentId + "</p>" +
                                "<br><p>¡Nos vemos en el show! 🤘</p>";

                emailService.enviarConfirmacion(email, asunto, mensajeHtml);

                log.info("📧 Email enviado correctamente a {}", email);

            } catch (Exception mailEx) {
                log.error("❌ ERROR enviando email", mailEx);
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("🔥 ERROR GENERAL en webhook", e);
            return ResponseEntity.ok("ERROR");
        }
    }
}