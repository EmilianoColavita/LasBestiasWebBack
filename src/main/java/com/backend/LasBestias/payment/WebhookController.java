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
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> data) {

        System.out.println("🔔 Webhook recibido: " + data);

        String paymentId = null;

        // Caso 1 → topic=payment y resource = ID directo
        if ("payment".equals(data.get("topic")) && data.get("resource") != null) {
            paymentId = data.get("resource").toString();
        }

        // Caso 2 → type=payment y data.id
        if ("payment".equals(data.get("type"))) {
            Map<String, Object> datosPago = (Map<String, Object>) data.get("data");
            if (datosPago != null && datosPago.get("id") != null) {
                paymentId = datosPago.get("id").toString();
            }
        }

        // Caso 3 → ignorar merchant_order
        if ("merchant_order".equals(data.get("topic"))) {
            System.out.println("⚠ Ignorando merchant_order");
            return ResponseEntity.ok("IGNORED");
        }

        if (paymentId == null) {
            System.out.println("❌ No se encontró paymentId");
            return ResponseEntity.ok("NO_PAYMENT_ID");
        }

        System.out.println("✔ paymentId detectado: " + paymentId);

        // Consultar pago en MP
        RestTemplate rest = new RestTemplate();
        String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = rest.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> mpPayment = response.getBody();
        System.out.println("📦 Pago desde MP: " + mpPayment);

        if (!"approved".equals(mpPayment.get("status"))) {
            System.out.println("⚠ Pago no aprobado. Status: " + mpPayment.get("status"));
            return ResponseEntity.ok("NOT_APPROVED");
        }

        // Extraer metadata
        Map<String, Object> metadata = (Map<String, Object>) mpPayment.get("metadata");

        String email = (String) metadata.get("email");
        String nombre = (String) metadata.get("nombre");
        String apellido = (String) metadata.get("apellido");
        Long eventoId = Long.valueOf(metadata.get("evento_id").toString());

        // Guardar en BD
        Entrada entrada = new Entrada();
        entrada.setEventoId(eventoId);
        entrada.setEmail(email);
        entrada.setNombre(nombre);
        entrada.setApellido(apellido);
        entrada.setPaymentId(paymentId);
        entrada.setFechaCompra(LocalDateTime.now());

        entradaService.registrarEntrada(entrada);

        System.out.println("✔ Entrada registrada");

        // Enviar email
        String asunto = "Confirmación de compra - Las Bestias";
        String mensajeHtml =
                "<h1>¡Gracias por tu compra!</h1>" +
                        "<p>Hola " + nombre + " " + apellido + ", tu entrada fue confirmada.</p>" +
                        "<p><strong>Evento ID:</strong> " + eventoId + "</p>" +
                        "<p><strong>Payment ID:</strong> " + paymentId + "</p>" +
                        "<p>Nos vemos pronto 🤘🔥</p>";

        emailService.enviarConfirmacion(email, asunto, mensajeHtml);

        return ResponseEntity.ok("OK");
    }

}

