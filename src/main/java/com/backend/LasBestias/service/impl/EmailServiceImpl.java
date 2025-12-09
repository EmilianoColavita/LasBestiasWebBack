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
    public void enviarConfirmacion(String destinatario, String asunto, String mensajeHtml) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensajeHtml, true); // TRUE → HTML

            mailSender.send(message);

            System.out.println("✔ Email enviado a " + destinatario);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error enviando email", e);
        }
    }
}
