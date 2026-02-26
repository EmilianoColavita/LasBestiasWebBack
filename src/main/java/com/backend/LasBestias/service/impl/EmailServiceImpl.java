package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        // no lo usamos más pero lo dejamos por compatibilidad
    }

    @Override
    public void enviarMultiplesQR(String destinatario,
                                  String asunto,
                                  String mensajeHtml,
                                  List<byte[]> qrImages,
                                  byte[] pdf) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensajeHtml, true);

            // QR inline + adjuntos
            for (int i = 0; i < qrImages.size(); i++) {

                // inline
                helper.addInline(
                        "qrImage" + i,
                        new ByteArrayResource(qrImages.get(i)),
                        "image/png"
                );

                // adjunto descargable
                helper.addAttachment(
                        "Entrada-" + (i + 1) + ".png",
                        new ByteArrayResource(qrImages.get(i))
                );
            }

            // adjuntar PDF ticket
            helper.addAttachment(
                    "Entradas-LasBestias.pdf",
                    new ByteArrayResource(pdf)
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando email", e);
        }
    }
}