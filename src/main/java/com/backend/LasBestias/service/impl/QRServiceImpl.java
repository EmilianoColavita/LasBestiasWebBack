package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.QRService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class QRServiceImpl implements QRService {

    @Override
    public byte[] generarQR(String contenido) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix =
                    writer.encode(contenido, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream pngOutputStream =
                    new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                    bitMatrix,
                    "PNG",
                    pngOutputStream
            );

            return pngOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando QR", e);
        }
    }
}