package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.service.TicketPDFService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class TicketPDFServiceImpl implements TicketPDFService {

    @Override
    public byte[] generarPDF(String nombre,
                             String evento,
                             int cantidad,
                             List<String> codigosQR) {

        try {

            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 12);
            Font codigoFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.DARK_GRAY);

            Paragraph tituloPdf = new Paragraph("LAS BESTIAS", titulo);
            tituloPdf.setAlignment(Element.ALIGN_CENTER);
            document.add(tituloPdf);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Evento: " + evento, normal));
            document.add(new Paragraph("Comprador: " + nombre, normal));
            document.add(new Paragraph("Cantidad: " + cantidad, normal));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Codigos de entrada:", normal));
            document.add(new Paragraph(" "));

            for (String codigo : codigosQR) {
                document.add(new Paragraph("• " + codigo, codigoFont));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Presentar QR en puerta.", normal));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}