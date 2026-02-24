package com.backend.LasBestias.service;

import java.io.ByteArrayOutputStream;

public interface QRService {
    byte[] generarQR(String contenido);
}