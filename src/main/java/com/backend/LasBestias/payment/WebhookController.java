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

    // MercadoPago hace un GET primero -> responde OK
    @GetMapping("/webhook")
    public ResponseEntity<String> verify() {
        return ResponseEntity.ok("Webhook OK");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> body) {

        try {
            System.out.println("🔔 Webhook recibido: " + body);

            // Validar tipo de evento
            String type = (String) body.get("type");
            if (!"payment".equals(type)) {
                System.out.println("⚠ Webhook ignorado (no es payment)");
                return ResponseEntity.ok("IGNORED");
            }

            // Extraer ID dentro de "data"
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data == null || data.get("id") == null) {
                System.out.println("⚠ Webhook incompleto: falta data.id");
                return ResponseEntity.ok("NO DATA ID");
            }

            String paymentId = data.get("id").toString();
            System.out.println("💳 Payment ID recibido: " + paymentId);

            // Llamar API de MercadoPago para obtener detalles
            RestTemplate rest = new RestTemplate();
            String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = rest.exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, Object> mpPayment = response.getBody();
            if (mpPayment == null) {
                System.out.println("❌ Error: no se pudo obtener info del pago");
                return ResponseEntity.ok("NO PAYMENT INFO");
            }

            String status = (String) mpPayment.get("status");
            if (!"approved".equals(status)) {
                System.out.println("⚠ Pago no aprobado: " + status);
                return ResponseEntity.ok("PAYMENT NOT APPROVED");
            }

            // Metadata enviada desde tu frontend
            Map<String, Object> metadata = (Map<String, Object>) mpPayment.get("metadata");

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

            // Envío de email
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

