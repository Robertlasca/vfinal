package com.residencia.restaurante.proyecto.serviceImpl;
import com.residencia.restaurante.proyecto.entity.Almacen;
import com.residencia.restaurante.proyecto.entity.Inventario;
import com.residencia.restaurante.proyecto.repository.IAlmacenRepository;
import com.residencia.restaurante.proyecto.repository.IInventarioRepository;
import org.springframework.http.HttpHeaders;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.residencia.restaurante.proyecto.entity.ProductoNormal;
import com.residencia.restaurante.proyecto.repository.IProductoNormalRepository;
import com.residencia.restaurante.proyecto.service.IReporteService;
import com.residencia.restaurante.security.model.Usuario;
import com.residencia.restaurante.security.repository.IUsuarioRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class IReporteServiceImpl implements IReporteService {
    @Autowired
    private IProductoNormalRepository productoNormalRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IAlmacenRepository almacenRepository;
    @Autowired
    private IInventarioRepository inventarioRepository;

    @Override
    public ResponseEntity<InputStreamResource> downloadPDF(Map<String,String> objetoMap) {
        // Generar el PDF
        ByteArrayInputStream bis = generarReporteProductosAgotandose(objetoMap);

        // Configurar los headers para la descarga del archivo
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @Override
    public ResponseEntity<InputStreamResource> descargarReporteInventarioAgotandose(Map<String, String> objetoMap) {
        // Generar el PDF
        ByteArrayInputStream bis = generarReporteInventarioAgotandose(objetoMap);

        // Configurar los headers para la descarga del archivo
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=inventarioAgotado.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @Override
    public ResponseEntity<InputStreamResource> descargarReporteInventarioAgotandoseXAlmacen(Map<String, String> objetoMap) {
        // Generar el PDF
        ByteArrayInputStream bis = generarReporteInventarioAgotandoseXAlmacen(objetoMap);

        // Configurar los headers para la descarga del archivo
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=inventarioAgotado.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private ByteArrayInputStream generarReporteInventarioAgotandoseXAlmacen(Map<String, String> objetoMap) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(Integer.parseInt(objetoMap.get("idUsuario")));


        Usuario usuario = new Usuario();
        if (usuarioOptional.isPresent()) {
            usuario = usuarioOptional.get();
        }

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Agregar logo
            Image logo = Image.getInstance("uploads/images/logo.jpg");
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            // Nombre del restaurante
            document.add(new Paragraph("Plazita Gourmet",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK)));
            document.add(new Paragraph(" ")); // Espacio

            // Encabezado del reporte
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String responsable = usuario.getNombre();  // Cambiar según sea necesario

            document.add(new Paragraph("Reporte de Inventario a Punto de Agotarse",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK)));
            document.add(new Paragraph("Fecha y Hora: " + dateFormat.format(date),
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Nombre del Responsable: " + responsable,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Información de contacto del restaurante
            document.add(new Paragraph("Dirección: Luis G. Urbina 108, FERROCARRIL, Col del Periodista, 68060 Oaxaca de Juárez, Oax.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Teléfono: +52 9514526384",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Correo: contacto@plazitagourmet.com",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Resumen Ejecutivo",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Descripción General: Este reporte muestra las materias primas que están cerca de alcanzar su stock mínimo, separadas por almacén, necesitando atención inmediata para evitar la interrupción de producción.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Puntos Críticos: Consulte las materias primas listadas en la tabla y tome las medidas necesarias para reabastecerlas.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));



            Optional<Almacen> almacenOptional= almacenRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if (almacenOptional.isPresent()) {

                    System.out.println("Entre");
                    List<Inventario> inventarios=inventarioRepository.inventariosInsuficientePorAlmacen(almacenOptional.get().getId());
                    if(!inventarios.isEmpty()){
                        System.out.println("Entre 1");
                        document.add(new Paragraph("Almacén "+almacenOptional.get().getNombre(),
                                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));

                        document.add(new Paragraph(" "));

                        // Crear la tabla
                        float[] columnWidths = {1, 3, 3, 2, 2, 2};
                        PdfPTable table = new PdfPTable(columnWidths);
                        table.setWidthPercentage(100);
                        //Agregando cabeceras de la tabla
                        PdfPCell cell;

                        cell = new PdfPCell(new Phrase("ID", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Materia prima", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Unidad Medida", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Stock Actual", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Stock Mínimo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Stock Máximo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        for (Inventario inventario:inventarios) {
                            table.addCell(String.valueOf(inventario.getId()));
                            table.addCell(inventario.getMateriaPrima().getNombre());
                            table.addCell(inventario.getMateriaPrima().getUnidadMedida());
                            table.addCell(String.valueOf(inventario.getStockActual()));
                            table.addCell(String.valueOf(inventario.getStockMin()));
                            table.addCell(String.valueOf(inventario.getStockMax()));

                        }

                        document.add(table);

                        // Añadir espacio
                        document.add(new Paragraph(" "));
                    }


                }


            // Comentarios y Recomendaciones
            String comentarios = objetoMap.containsKey("comentarios") ? objetoMap.get("comentarios") : "";
            String recomendaciones = objetoMap.containsKey("recomendaciones") ? objetoMap.get("recomendaciones") : "";

            document.add(new Paragraph("Comentarios y Recomendaciones",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Comentarios: " + comentarios,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Recomendaciones: " + recomendaciones,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Sección de Validación
            document.add(new Paragraph("Sección de Validación",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Aprobaciones: ________________________________________________________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            String comentariosAdicionales = objetoMap.containsKey("comentariosAdicionales") ? objetoMap.get("comentariosAdicionales") : "";
            document.add(new Paragraph("Comentarios Adicionales: " + comentariosAdicionales,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Espacio extra para mejor separación visual.
            document.add(new Paragraph(" "));

            // Cierre del documento
            document.add(new Paragraph("Gracias por su atención.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Firma del Responsable: __________________________________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private ByteArrayInputStream generarReporteInventarioAgotandose(Map<String, String> objetoMap) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(Integer.parseInt(objetoMap.get("idUsuario")));
        List<Almacen> almacens= almacenRepository.findAll();

        Usuario usuario = new Usuario();
        if (usuarioOptional.isPresent()) {
            usuario = usuarioOptional.get();
        }

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Agregar logo
            Image logo = Image.getInstance("uploads/images/logo.jpg");
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            // Nombre del restaurante
            document.add(new Paragraph("Plazita Gourmet",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK)));
            document.add(new Paragraph(" ")); // Espacio

            // Encabezado del reporte
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String responsable = usuario.getNombre();  // Cambiar según sea necesario

            document.add(new Paragraph("Reporte de Inventario a Punto de Agotarse",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK)));
            document.add(new Paragraph("Fecha y Hora: " + dateFormat.format(date),
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Nombre del Responsable: " + responsable,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Información de contacto del restaurante
            document.add(new Paragraph("Dirección: Luis G. Urbina 108, FERROCARRIL, Col del Periodista, 68060 Oaxaca de Juárez, Oax.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Teléfono: +52 9514526384",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Correo: contacto@plazitagourmet.com",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Resumen Ejecutivo",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Descripción General: Este reporte muestra las materias primas que están cerca de alcanzar su stock mínimo, separadas por almacén, necesitando atención inmediata para evitar la interrupción de producción.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Puntos Críticos: Consulte las materias primas listadas en la tabla y tome las medidas necesarias para reabastecerlas.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));



            if(!almacens.isEmpty()){
                for (Almacen almacen:almacens) {
                    System.out.println("Entre");
                    List<Inventario> inventarios=inventarioRepository.inventariosInsuficientePorAlmacen(almacen.getId());
                    if(!inventarios.isEmpty()){
                        System.out.println("Entre 1");
                        document.add(new Paragraph("Almacén "+almacen.getNombre(),
                                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));

                        document.add(new Paragraph(" "));

                        // Crear la tabla
                        float[] columnWidths = {1, 3, 3, 2, 2, 2};
                        PdfPTable table = new PdfPTable(columnWidths);
                        table.setWidthPercentage(100);
                        //Agregando cabeceras de la tabla
                        PdfPCell cell;

                        cell = new PdfPCell(new Phrase("ID", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Materia prima", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Unidad Medida", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Stock Actual", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Stock Mínimo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase("Stock Máximo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        table.addCell(cell);

                        for (Inventario inventario:inventarios) {
                            table.addCell(String.valueOf(inventario.getId()));
                            table.addCell(inventario.getMateriaPrima().getNombre());
                            table.addCell(inventario.getMateriaPrima().getUnidadMedida());
                            table.addCell(String.valueOf(inventario.getStockActual()));
                            table.addCell(String.valueOf(inventario.getStockMin()));
                            table.addCell(String.valueOf(inventario.getStockMax()));

                        }

                        document.add(table);

                        // Añadir espacio
                        document.add(new Paragraph(" "));
                    }


                }
            }

            // Comentarios y Recomendaciones
            String comentarios = objetoMap.containsKey("comentarios") ? objetoMap.get("comentarios") : "";
            String recomendaciones = objetoMap.containsKey("recomendaciones") ? objetoMap.get("recomendaciones") : "";

            document.add(new Paragraph("Comentarios y Recomendaciones",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Comentarios: " + comentarios,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Recomendaciones: " + recomendaciones,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Sección de Validación
            document.add(new Paragraph("Sección de Validación",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Aprobaciones: ________________________________________________________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            String comentariosAdicionales = objetoMap.containsKey("comentariosAdicionales") ? objetoMap.get("comentariosAdicionales") : "";
            document.add(new Paragraph("Comentarios Adicionales: " + comentariosAdicionales,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Espacio extra para mejor separación visual.
            document.add(new Paragraph(" "));

            // Cierre del documento
            document.add(new Paragraph("Gracias por su atención.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Firma del Responsable: __________________________________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());



    }

    @Override
    public ByteArrayInputStream generarReporteProductosAgotandose(Map<String,String> objetoMap) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(Integer.parseInt(objetoMap.get("idUsuario")));
        List<ProductoNormal> productos = productoNormalRepository.findProductoNormalByStockActualMenorAlMinimo();

        Usuario usuario = new Usuario();
        if (usuarioOptional.isPresent()) {
            usuario = usuarioOptional.get();
        }

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Agregar logo
            Image logo = Image.getInstance("uploads/images/logo.jpg");
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            // Nombre del restaurante
            document.add(new Paragraph("Plazita Gourmet",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK)));
            document.add(new Paragraph(" ")); // Espacio

            // Encabezado del reporte
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String responsable = usuario.getNombre();  // Cambiar según sea necesario

            document.add(new Paragraph("Reporte de Productos a Punto de Agotarse",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK)));
            document.add(new Paragraph("Fecha y Hora: " + dateFormat.format(date),
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Nombre del Responsable: " + responsable,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Información de contacto del restaurante
            document.add(new Paragraph("Dirección: Luis G. Urbina 108, FERROCARRIL, Col del Periodista, 68060 Oaxaca de Juárez, Oax.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Teléfono: +52 9514526384",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Correo: contacto@plazitagourmet.com",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Resumen ejecutivo
            document.add(new Paragraph("Resumen Ejecutivo",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Descripción General: Este reporte muestra los productos que están cerca de alcanzar su stock mínimo, necesitando atención inmediata para evitar la ruptura de stock.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Puntos Críticos: Consultar los productos listados en la tabla y realizar las acciones necesarias para reabastecerlos.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Crear la tabla
            float[] columnWidths = {1, 3, 3, 2, 2, 2};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            // Agregando cabeceras de la tabla
            PdfPCell cell;

            cell = new PdfPCell(new Phrase("ID", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Nombre", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Descripción", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Stock Actual", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Stock Mínimo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Stock Máximo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

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
            String comentarios = objetoMap.containsKey("comentarios") ? objetoMap.get("comentarios") : "";
            String recomendaciones = objetoMap.containsKey("recomendaciones") ? objetoMap.get("recomendaciones") : "";

            document.add(new Paragraph("Comentarios y Recomendaciones",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Comentarios: " + comentarios,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Recomendaciones: " + recomendaciones,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Añadir espacio
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Sección de Validación
            document.add(new Paragraph("Sección de Validación",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            document.add(new Paragraph("Aprobaciones: ________________________________________________________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            String comentariosAdicionales = objetoMap.containsKey("comentariosAdicionales") ? objetoMap.get("comentariosAdicionales") : "";
            document.add(new Paragraph("Comentarios Adicionales: " + comentariosAdicionales,
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            // Espacio extra para mejor separación visual.
            document.add(new Paragraph(" "));

            // Cierre del documento
            document.add(new Paragraph("Gracias por su atención.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            document.add(new Paragraph("Firma del Responsable: __________________________________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());

    }
}
