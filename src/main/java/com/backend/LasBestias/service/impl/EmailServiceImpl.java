package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarConfirmacion(String destinatario,
                                   String asunto,
                                   String mensajeHtml,
                                   byte[] qrImage) {
        // Método legacy (no usado actualmente)
        log.info("enviarConfirmacion() no utilizado actualmente");
    }

    @Override
    public void enviarMultiplesQR(String destinatario,
                                  String asunto,
                                  String mensajeHtml,
                                  List<byte[]> qrImages,
                                  byte[] pdf) {

        try {

            log.info("📧 Enviando email con entradas a {}", destinatario);

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("lasbestiasok@gmail.com");   // 🔥 IMPORTANTE
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensajeHtml, true);

            // QR inline + adjuntos
            for (int i = 0; i < qrImages.size(); i++) {

                helper.addInline(
                        "qrImage" + i,
                        new ByteArrayResource(qrImages.get(i)),
                        "image/png"
                );

                helper.addAttachment(
                        "Entrada-" + (i + 1) + ".png",
                        new ByteArrayResource(qrImages.get(i))
                );
            }

            // PDF con todas las entradas
            helper.addAttachment(
                    "Entradas-LasBestias.pdf",
                    new ByteArrayResource(pdf)
            );

            mailSender.send(message);

            log.info("✅ Email enviado correctamente a {}", destinatario);

        } catch (MessagingException e) {

            log.error("❌ Error enviando email a {}", destinatario, e);

            throw new RuntimeException("Error enviando email", e);
        }
    }

    @Override
    public void enviarMensajeContacto(String nombre,
                                      String email,
                                      String mensaje) {

        try {

            log.info("📧 Nuevo mensaje de contacto desde {}", email);

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("lasbestiasok@gmail.com");   // 🔥 IMPORTANTE
            helper.setTo("lasbestiasok@gmail.com");

            helper.setSubject("Nuevo mensaje desde la web - Las Bestias");

            String html =
                    "<h2>Nuevo mensaje desde la web</h2>" +
                            "<p><b>Nombre:</b> " + nombre + "</p>" +
                            "<p><b>Email:</b> " + email + "</p>" +
                            "<p><b>Mensaje:</b></p>" +
                            "<p>" + mensaje + "</p>";

            helper.setText(html, true);

            mailSender.send(message);

            log.info("✅ Mensaje de contacto enviado correctamente");

        } catch (MessagingException e) {

            log.error("❌ Error enviando mensaje de contacto", e);

            throw new RuntimeException("Error enviando mensaje de contacto", e);
        }
    }
}