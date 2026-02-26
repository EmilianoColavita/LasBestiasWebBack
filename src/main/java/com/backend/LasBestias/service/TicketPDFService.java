package com.backend.LasBestias.service;

import java.util.List;

public interface TicketPDFService {

    byte[] generarPDF(String nombre,
                      String evento,
                      int cantidad,
                      List<String> codigosQR);
}