package com.residencia.restaurante.proyecto.serviceImpl;

import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.residencia.restaurante.proyecto.entity.ProductoNormal;
import com.residencia.restaurante.proyecto.repository.IProductoNormalRepository;
import com.residencia.restaurante.proyecto.service.IReporteService;
import org.springframework.stereotype.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.util.Map;

@Service
public class IReporteServiceImpl implements IReporteService {
    @Autowired
    private IProductoNormalRepository productoNormalRepository;

    @Override
    public ByteArrayInputStream generarReporteProductosAgotandose(Map<String,String> objetoMap) {
        List<ProductoNormal> productos = productoNormalRepository.findProductoNormalByStockActualMenorAlMinimo();

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Encabezado del reporte
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String responsable = "Sistema de Gestión de Inventario";  // Cambiar según sea necesario

            document.add(new Paragraph("Reporte de Productos a Punto de Agotarse",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph("Fecha y Hora: " + dateFormat.format(date),
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph("Nombre del Responsable: " + responsable,
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Resumen ejecutivo
            document.add(new Paragraph("Resumen Ejecutivo",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Descripción General: Este reporte muestra los productos que están cerca de alcanzar su stock mínimo, necesitando atención inmediata para evitar la ruptura de stock.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph("Puntos Críticos: Consultar los productos listados en la tabla y realizar las acciones necesarias para reabastecerlos.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Crear la tabla
            float[] columnWidths = {1, 3, 3, 2, 2, 2};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            // Agregando cabeceras de la tabla
            table.addCell(new PdfPCell(new Phrase("ID")));
            table.addCell(new PdfPCell(new Phrase("Nombre")));
            table.addCell(new PdfPCell(new Phrase("Descripción")));
            table.addCell(new PdfPCell(new Phrase("Stock Actual")));
            table.addCell(new PdfPCell(new Phrase("Stock Mínimo")));
            table.addCell(new PdfPCell(new Phrase("Stock Máximo")));

            // Llenando la tabla con datos de los productos
            for (ProductoNormal producto : productos) {
                table.addCell(String.valueOf(producto.getId()));
                table.addCell(producto.getNombre());
                table.addCell(producto.getDescripcion());
                table.addCell(String.valueOf(producto.getStockActual()));
                table.addCell(String.valueOf(producto.getStockMin()));
                table.addCell(String.valueOf(producto.getStockMax()));
            }

            document.add(table);
            // Comentarios y Recomendaciones
            document.add(new Paragraph("Comentarios y Recomendaciones",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Comentarios: Aqui van los comentarios.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph("Recomendaciones: Aqui van las recomendaciones.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));

// Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

// Sección de Validación
            document.add(new Paragraph("Sección de Validación",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Aprobaciones: Aqui va la firma ________________________________________________________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph("Comentarios Adicionales: __________________________________________________________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));

            document.add(new Paragraph(" ")); // Espacio extra para mejor separación visual.




            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
