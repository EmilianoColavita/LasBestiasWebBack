package com.backend.LasBestias.payment;

import com.backend.LasBestias.model.Entrada;
import com.backend.LasBestias.service.EntradaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@Slf4j
@RequiredArgsConstructor
public class WebhookController {

    private final EntradaService entradaService;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/webhook")
    public String webhook(@RequestBody Map<String, Object> body) {

        log.info("🔔 Webhook recibido: {}", body);

        try {
            String topic = (String) body.get("topic");

            // SI ES merchant_order → NO HACER NADA
            if ("merchant_order".equals(topic)) {
                log.info("ℹ Notificación merchant_order ignorada.");
                return "OK";
            }

            // SI ES payment → procesamos
            if ("payment".equals(topic) || "payment.created".equals(body.get("type"))) {

                Object dataObj = body.get("data");
                if (dataObj == null) {
                    log.error("❌ No hay data -> no se puede procesar");
                    return "OK";
                }

                Map<String, Object> data = (Map<String, Object>) dataObj;

                // ID de pago
                Object paymentIdObj = data.get("id");
                if (paymentIdObj == null) {
                    log.error("❌ No se encontró paymentId");
                    return "OK";
                }

                String paymentId = paymentIdObj.toString();
                log.info("✔ paymentId detectado: {}", paymentId);

                // Obtener datos completos desde Mercado Pago
                Map<String, Object> paymentInfo = mpClient.getPayment(paymentId);
                log.info("📦 Pago desde MP: {}", paymentInfo);

                String status = (String) paymentInfo.get("status");
                if (!"approved".equals(status)) {
                    log.info("⏳ Pago no aprobado todavía ({})", status);
                    return "OK";
                }

                // EXTRAER metadata
                Map<String, Object> metadata = (Map<String, Object>) paymentInfo.get("metadata");
                if (metadata == null) {
                    log.error("❌ No había metadata en el pago.");
                    return "OK";
                }

                // EVITAR DUPLICADOS
                if (entradaService.existsByPaymentId(paymentId)) {
                    log.warn("⚠ Entrada ya registrada → evitando duplicado. ({})", paymentId);
                    return "OK";
                }

                // CREAR ENTRADA
                Entrada entrada = new Entrada();
                entrada.setEventoId(Long.valueOf(metadata.get("evento_id").toString()));
                entrada.setNombre((String) metadata.get("nombre"));
                entrada.setApellido((String) metadata.get("apellido"));
                entrada.setEmail((String) metadata.get("email"));
                entrada.setPaymentId(paymentId);
                entrada.setFechaCompra(LocalDateTime.now());

                entradaService.registrarEntrada(entrada);

                log.info("🎟 Entrada registrada correctamente para {}", entrada.getEmail());
            }

            return "OK";

        } catch (Exception e) {
            log.error("🔥 ERROR en webhook: ", e);
            return "ERROR";
        }
    }
}
