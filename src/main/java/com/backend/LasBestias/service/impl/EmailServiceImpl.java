package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensajeHtml, true);

            helper.addInline(
                    "qrImage",
                    new org.springframework.core.io.ByteArrayResource(qrImage),
                    "image/png"
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando email", e);
        }
    }
}
