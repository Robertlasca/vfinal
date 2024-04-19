package com.residencia.restaurante.proyecto.controllerImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.controller.IUtensilioController;
import com.residencia.restaurante.proyecto.entity.Utensilio;
import com.residencia.restaurante.proyecto.service.IUtensilioService;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementación del controlador para la gestión de utensilios en el restaurante.
 */
@RestController
public class UtensilioControllerImpl implements IUtensilioController {

    @Autowired
    IUtensilioService utensilioService;

    /**
     * Agrega un nuevo utensilio.
     *
     * @param objetoMap Mapa que contiene los datos del utensilio a agregar.
     * @return ResponseEntity con el resultado de la operación y el estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<String> agregar(Map<String, String> objetoMap) {
        try {
            return utensilioService.agregar(objetoMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> agregarUtensilio(String nombre, String descripcion, int idCocina, int cantidad, MultipartFile file) {
        try {
            return utensilioService.agregarUtensilio(nombre,descripcion,idCocina,cantidad,file);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> actualizarUtensilio(Integer id, String nombre, String descripcion, int idCocina, int cantidad, MultipartFile file) {
        try {
            return utensilioService.actualizarUtensilio(id,nombre,descripcion,idCocina,cantidad,file);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Actualiza un utensilio existente.
     *
     * @param objetoMap Mapa que contiene los nuevos datos del utensilio a actualizar.
     * @return ResponseEntity con el resultado de la operación y el estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<String> actualizar(Map<String, String> objetoMap) {
        try {
            return utensilioService.actualizar(objetoMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Elimina un utensilio por su ID.
     *
     * @param id ID del utensilio a eliminar.
     * @return ResponseEntity con el resultado de la operación y el estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<String> eliminar(Integer id) {
        try {
            return utensilioService.eliminar(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Obtiene la lista de utensilios asociados a una cocina específica.
     *
     * @param id ID de la cocina para la cual se desean obtener los utensilios.
     * @return ResponseEntity con la lista de utensilios y el estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Utensilio>> obtenerUtensiliosXCocina(Integer id) {
        try {
            return utensilioService.obtenerUtensiliosXCocina(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Obtiene la lista de todos los utensilios.
     *
     * @return ResponseEntity con la lista de utensilios y el estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Utensilio>> obtenerUtensilios() {
        try {
            return utensilioService.obtenerUtensilios();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Obtiene un utensilio por su ID.
     *
     * @param id ID del utensilio a obtener.
     * @return ResponseEntity con el utensilio solicitado y el estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<Utensilio> obtenerPorId(Integer id) {
        try {
            return utensilioService.obtenerUtensilioId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new Utensilio(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
