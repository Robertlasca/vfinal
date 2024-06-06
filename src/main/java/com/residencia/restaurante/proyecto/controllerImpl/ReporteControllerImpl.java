package com.residencia.restaurante.proyecto.controllerImpl;

import com.residencia.restaurante.proyecto.controller.IReporteController;
import com.residencia.restaurante.proyecto.service.IReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.Map;

@RestController
public class ReporteControllerImpl implements IReporteController {
    @Autowired
    private IReporteService reporteService;

    @Override
    public ResponseEntity<byte[]> descargarReporteProductosAgotandose(Map<String,String> objetoMap) {
        ByteArrayInputStream streamReporte = reporteService.generarReporteProductosAgotandose(objetoMap);

        byte[] datosPdf = streamReporte.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_productos_agotandose.pdf");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(datosPdf);
    }
}
