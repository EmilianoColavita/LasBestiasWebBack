package com.backend.LasBestias.service;

import java.util.List;

public interface EmailService {

    void enviarConfirmacion(String destinatario,
                            String asunto,
                            String mensajeHtml,
                            byte[] qrImage);

    void enviarMultiplesQR(String destinatario,
                           String asunto,
                           String mensajeHtml,
                           List<byte[]> qrImages,
                           byte[] pdf);
}