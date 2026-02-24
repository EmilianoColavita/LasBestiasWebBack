package com.backend.LasBestias.service;

public interface EmailService {
    void enviarConfirmacion(String destinatario,
                            String asunto,
                            String mensajeHtml,
                            byte[] qrImage);
}
