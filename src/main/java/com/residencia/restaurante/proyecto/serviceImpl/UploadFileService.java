package com.residencia.restaurante.proyecto.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Servicio para la carga y eliminación de archivos.
 */
@Service
public class UploadFileService {

    private static final Logger logger =LoggerFactory.getLogger(UploadFileService.class);

    private final String folder = "src/main/resources/static/images/";

    public String guardarImagen(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Path directoryPath = Paths.get(folder);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                logger.info("Directorio creado: " + directoryPath.toString());
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + fileName);
            Files.write(path, file.getBytes());
            logger.info("Archivo guardado: " + path.toString());
            return fileName;
        }
        return "default.jpg";
    }

    public void eliminarImagen(String nombre) {
        File file = new File(folder + nombre);
        if (file.exists()) {
            file.delete();
            logger.info("Archivo eliminado: " + nombre);
        } else {
            logger.warn("Archivo no encontrado para eliminar: " + nombre);
        }
    }
}