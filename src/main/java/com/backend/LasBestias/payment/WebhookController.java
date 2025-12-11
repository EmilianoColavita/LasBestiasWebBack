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

    @GetMapping("/webhook")
    public ResponseEntity<String> verify() {
        return ResponseEntity.ok("Webhook OK");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> body) {

        try {
            System.out.println("🔔 Webhook recibido: " + body);

            String paymentId = null;

            // Caso 1: data.id
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data != null && data.get("id") != null) {
                paymentId = data.get("id").toString();
            }

            // Caso 2: resource: /v1/payments/123
            if (paymentId == null && body.get("resource") != null) {
                String resource = body.get("resource").toString();
                if (resource.contains("/payments/")) {
                    paymentId = resource.substring(resource.lastIndexOf("/") + 1);
                }
            }

            if (paymentId == null) {
                System.out.println("❌ No se encontró paymentId");
                return ResponseEntity.ok("NO PAYMENT ID");
            }

            // ---- CONSULTAR API DE MERCADO PAGO ---- //
            RestTemplate rest = new RestTemplate();
            String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map> response =
                    rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            Map<String, Object> mpPayment = response.getBody();

            if (mpPayment == null) return ResponseEntity.ok("NO PAYMENT INFO");

            System.out.println("📦 Pago desde MP: " + mpPayment);

            // Validar estado
            if (!"approved".equals(mpPayment.get("status"))) {
                System.out.println("⚠ Pago NO aprobado");
                return ResponseEntity.ok("PAYMENT NOT APPROVED");
            }

            // EXTRAER METADATA
            Map<String, Object> metadata = (Map<String, Object>) mpPayment.get("metadata");
            if (metadata == null) return ResponseEntity.ok("NO METADATA");

            Long eventoId = Long.valueOf(metadata.get("eventoId").toString());
            String email = (String) metadata.get("email");
            String nombre = (String) metadata.get("nombre");
            String apellido = (String) metadata.get("apellido");

            // EVITAR DUPLICADOS
            if (entradaService.existePorPaymentId(paymentId)) {
                System.out.println("⚠ Entrada ya registrada. Se ignora.");
                return ResponseEntity.ok("ALREADY PROCESSED");
            }

            // ---- GUARDAR ENTRADA ---- //
            Entrada entrada = new Entrada();
            entrada.setEventoId(eventoId);
            entrada.setEmail(email);
            entrada.setNombre(nombre);
            entrada.setApellido(apellido);
            entrada.setPaymentId(paymentId);
            entrada.setFechaCompra(LocalDateTime.now());

            entradaService.registrarEntrada(entrada);

            // ---- ENVIAR EMAIL ---- //
            String asunto = "Confirmación de compra - Las Bestias";
            String mensajeHtml =
                    "<h1>¡Gracias por tu compra!</h1>" +
                            "<p>Hola " + nombre + " " + apellido + ", tu entrada fue confirmada.</p>" +
                            "<p><strong>Evento ID:</strong> " + eventoId + "</p>" +
                            "<p><strong>Payment ID:</strong> " + paymentId + "</p>" +
                            "<p>Nos vemos pronto 🤘🔥</p>";

            emailService.enviarConfirmacion(email, asunto, mensajeHtml);

            System.out.println("✔ Entrada registrada y email enviado");

            return ResponseEntity.ok("PROCESSED");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("ERROR");
        }
    }
}

