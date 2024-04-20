package com.residencia.restaurante.proyecto.serviceImpl;

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
    /**
     * Guarda una imagen en el directorio especificado.
     * @param file Archivo de imagen a guardar.
     * @return String Nombre del archivo guardado.
     * @throws IOException Si ocurre un error de E/S durante la escritura del archivo.
     */
    private String folder = "src/main/resources/static/images/";

    public String guardarImagen(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            // Verificar si el directorio existe, si no existe, crearlo
            Path directoryPath = Paths.get(folder);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(folder + file.getOriginalFilename());
            Files.write(path, bytes);
            return file.getOriginalFilename();
        }
        return "default.jpg";
    }
    /**
     * Elimina una imagen del directorio.
     * @param nombre Nombre del archivo a eliminar.
     */
    public void  eliminarImagen(String nombre){
        String ruta="src/main/resources/static/images/";
        File file=new File(ruta+nombre);
        file.delete();
    }

}
