package com.backend.LasBestias.payment;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.service.EntradaService;
import com.backend.LasBestias.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@Slf4j
public class WebhookController {

    private final String accessToken = System.getenv("MERCADOPAGO_ACCESS_TOKEN");
    private final EntradaService entradaService;
    private final EmailService emailService;

    public WebhookController(EntradaService entradaService, EmailService emailService) {
        this.entradaService = entradaService;
        this.emailService = emailService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> body) {

        log.info("🔔 Webhook recibido: {}", body);

        try {
            String topic = (String) body.get("topic");

            // IGNORAR MERCHANT_ORDER
            if ("merchant_order".equals(topic)) {
                log.info("ℹ Notificación merchant_order ignorada.");
                return ResponseEntity.ok("OK");
            }

            // Validar que tenga "data"
            if (!body.containsKey("data")) {
                log.warn("⚠ Webhook sin data → ignorado");
                return ResponseEntity.ok("OK");
            }

            Map<String, Object> data = (Map<String, Object>) body.get("data");

            // Obtener paymentId
            Object paymentIdObj = data.get("id");
            if (paymentIdObj == null) {
                log.error("❌ No se encontró paymentId en data");
                return ResponseEntity.ok("NO_PAYMENT_ID");
            }

            String paymentId = paymentIdObj.toString();
            log.info("✔ Payment ID detectado: {}", paymentId);

            // Evitar duplicados
            if (entradaService.existePorPaymentId(paymentId)) {
                log.warn("⚠ El pago ya fue procesado, evitando duplicado ({})", paymentId);
                return ResponseEntity.ok("DUPLICATE");
            }

            // Consultar el pago en MP
            RestTemplate rest = new RestTemplate();
            String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response =
                    rest.exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, Object> mpPayment = response.getBody();
            log.info("📦 Respuesta MP Payment: {}", mpPayment);

            if (mpPayment == null) {
                log.error("❌ No llegó info del pago desde Mercado Pago");
                return ResponseEntity.ok("NO_MP_DATA");
            }

            // Validar estado de pago
            String status = (String) mpPayment.get("status");
            if (!"approved".equals(status)) {
                log.info("⏳ Pago aún no aprobado ({})", status);
                return ResponseEntity.ok("NOT_APPROVED");
            }

            // METADATA
            Map<String, Object> metadata = (Map<String, Object>) mpPayment.get("metadata");
            if (metadata == null) {
                log.error("❌ El pago aprobado no contiene metadata");
                return ResponseEntity.ok("NO_METADATA");
            }

            Long eventoId = Long.valueOf(metadata.get("evento_id").toString());
            String email = metadata.get("email").toString();
            String nombre = metadata.get("nombre").toString();
            String apellido = metadata.get("apellido").toString();

            // Crear entrada
            Entrada entrada = new Entrada();
            entrada.setEventoId(eventoId);
            entrada.setEmail(email);
            entrada.setNombre(nombre);
            entrada.setApellido(apellido);
            entrada.setPaymentId(paymentId);
            entrada.setFechaCompra(LocalDateTime.now());

            entradaService.registrarEntrada(entrada);
            log.info("🎟 Entrada registrada correctamente");

            // Enviar email
            String asunto = "Confirmación de compra - Las Bestias";
            String mensajeHtml =
                    "<h1>¡Gracias por tu compra!</h1>" +
                            "<p>Hola " + nombre + " " + apellido + ", tu entrada fue confirmada.</p>" +
                            "<p><strong>Evento ID:</strong> " + eventoId + "</p>" +
                            "<p><strong>Payment ID:</strong> " + paymentId + "</p>";

            emailService.enviarConfirmacion(email, asunto, mensajeHtml);

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("🔥 ERROR en webhook", e);
            return ResponseEntity.ok("ERROR");
        }
    }
}
