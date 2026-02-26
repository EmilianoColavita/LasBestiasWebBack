package com.backend.LasBestias.payment;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.service.*;
import com.backend.LasBestias.service.dto.response.EventoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/pagos")
@Slf4j
public class WebhookController {

    @Value("${mercadopago.access_token}")
    private String accessToken;

    private final EntradaService entradaService;
    private final EmailService emailService;
    private final QRService qrService;
    private final EventoService eventoService;
    private final TicketPDFService ticketPDFService;

    public WebhookController(EntradaService entradaService,
                             EmailService emailService,
                             QRService qrService,
                             EventoService eventoService,
                             TicketPDFService ticketPDFService) {
        this.entradaService = entradaService;
        this.emailService = emailService;
        this.qrService = qrService;
        this.eventoService = eventoService;
        this.ticketPDFService = ticketPDFService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String id,
            @RequestBody(required = false) Map<String, Object> body) {

        try {

            String paymentId = null;

            // Obtener paymentId desde query params
            if ("payment".equals(topic) || "payment".equals(type)) {
                paymentId = id;
            }

            // Obtener paymentId desde body
            if (body != null && body.containsKey("data")) {
                Map<String, Object> data =
                        (Map<String, Object>) body.get("data");

                if (data.get("id") != null) {
                    paymentId = data.get("id").toString();
                }
            }

            if (paymentId == null) {
                return ResponseEntity.ok("NO_PAYMENT_ID");
            }

            // 🔐 Anti doble webhook
            if (entradaService.existePorPaymentId(paymentId)) {
                return ResponseEntity.ok("DUPLICATE");
            }

            // Consultar MercadoPago
            RestTemplate rest = new RestTemplate();
            String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response =
                    rest.exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, Object> mpPayment = response.getBody();

            if (mpPayment == null) return ResponseEntity.ok("NO_MP_DATA");

            if (!"approved".equals(mpPayment.get("status"))) {
                return ResponseEntity.ok("NOT_APPROVED");
            }

            // Obtener external_reference
            String externalRef =
                    (String) mpPayment.get("external_reference");

            if (externalRef == null) {
                return ResponseEntity.ok("NO_EXTERNAL_REF");
            }

            String[] parts = externalRef.split("\\|");

            Long eventoId = Long.valueOf(parts[0]);
            String email = parts[1];
            String nombre = parts[2];
            String telefono = parts[3];
            String dni = parts[4];
            Integer cantidad = Integer.valueOf(parts[5]);

            // Buscar evento correctamente con tu service
            EventoDTO evento = eventoService.getById(eventoId);

            if (evento == null) {
                return ResponseEntity.ok("EVENT_NOT_FOUND");
            }

            String nombreEvento = evento.getNombre();

            String asunto = "🎟 Tus entradas - Las Bestias";

            StringBuilder mensajeHtml = new StringBuilder();
            mensajeHtml.append("<h1>¡Gracias por tu compra!</h1>");
            mensajeHtml.append("<p>Hola ").append(nombre).append("</p>");
            mensajeHtml.append("<p>Evento: <b>")
                    .append(nombreEvento)
                    .append("</b></p>");
            mensajeHtml.append("<p>Cantidad: ")
                    .append(cantidad)
                    .append("</p><br>");

            List<byte[]> qrImages = new ArrayList<>();
            List<String> codigosQR = new ArrayList<>();

            for (int i = 0; i < cantidad; i++) {

                String qrToken = UUID.randomUUID().toString();
                codigosQR.add(qrToken);

                Entrada entrada = new Entrada();
                entrada.setEventoId(eventoId);
                entrada.setNombreComprador(nombre);
                entrada.setEmail(email);
                entrada.setTelefono(telefono);
                entrada.setDni(dni);
                entrada.setPaymentId(paymentId);
                entrada.setFechaCompra(LocalDateTime.now());
                entrada.setQrToken(qrToken);

                entradaService.guardar(entrada);

                byte[] qr = qrService.generarQR(qrToken);
                qrImages.add(qr);

                mensajeHtml.append("<img src='cid:qrImage")
                        .append(i)
                        .append("' width='250'/><br><br>");
            }

            // Generar PDF ticket
            byte[] pdf = ticketPDFService.generarPDF(
                    nombre,
                    nombreEvento,
                    cantidad,
                    codigosQR
            );

            // Enviar email
            emailService.enviarMultiplesQR(
                    email,
                    asunto,
                    mensajeHtml.toString(),
                    qrImages,
                    pdf
            );

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Error en webhook", e);
            return ResponseEntity.ok("ERROR");
        }
    }
}