package com.residencia.restaurante.proyecto.controller;

import com.residencia.restaurante.proyecto.dto.AlmacenDTO;
import com.residencia.restaurante.proyecto.dto.EstacionDTO;
import com.residencia.restaurante.proyecto.dto.UtensilioDTO;
import com.residencia.restaurante.proyecto.entity.Cocina_Utensilio;
import com.residencia.restaurante.proyecto.entity.Utensilio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Interfaz del controlador para la gestión de utensilios en el restaurante.
 * Proporciona endpoints para agregar, actualizar, eliminar y obtener información sobre utensilios.
 */
@RequestMapping(path = "/utensilio")
public interface IUtensilioController {


    @PostMapping(path = "/agregarUtensilio", consumes = "multipart/form-data")
    ResponseEntity<String> agregarUtensilio(@RequestParam("nombre") String nombre,
                                            @RequestParam("descripcion") String descripcion,
                                            @RequestPart("inventario")String inventario,
                                            @RequestParam(value = "img",required = false) MultipartFile file);

    @PostMapping(path = "/actualizarUtensilio")
    ResponseEntity<String> actualizarUtensilio(
            @RequestParam("id")Integer id,
            @RequestParam("nombre") String nombre,
                                            @RequestParam("descripcion") String descripcion,
                                            @RequestParam(value = "img",required = false) MultipartFile file);

    /**
     * Actualiza los datos de un utensilio existente en el sistema. La información para la actualización
     * se pasa en el cuerpo de la solicitud.
     *
     * @param objetoMap Un mapa que contiene los datos necesarios para actualizar el utensilio.
     * @return ResponseEntity con un mensaje indicando el resultado de la operación y el estado HTTP.
     */

    /**
     * Elimina un utensilio del sistema según su identificador.
     *
     * @param id El identificador único del utensilio a eliminar.
     * @return ResponseEntity con un mensaje indicando el resultado de la operación y el estado HTTP.
     */
    @PostMapping(path = "/eliminar/{id}")
    ResponseEntity<String> eliminar(@PathVariable Integer id);

    /**
     * Obtiene una lista de utensilios asociados a una cocina específica.
     *
     * @param id El identificador único de la cocina.
     * @return ResponseEntity con la lista de utensilios asociados a la cocina y el estado HTTP correspondiente.
     */
    @GetMapping(path = "/XCocina/{id}")
    ResponseEntity<List<Cocina_Utensilio>> obtenerUtensiliosXCocina(@PathVariable Integer id);

    /**
     * Obtiene una lista de todos los utensilios registrados en el sistema.
     *
     * @return ResponseEntity con la lista completa de utensilios y el estado HTTP correspondiente.
     */
    @GetMapping(path = "/obtenerUtensilios")
    ResponseEntity<List<UtensilioDTO>> obtenerUtensilios();

    /**
     * Obtiene los detalles de un utensilio específico por su identificador.
     *
     * @param id El identificador único del utensilio a consultar.
     * @return ResponseEntity con los detalles del utensilio solicitado y el estado HTTP correspondiente.
     */
    @GetMapping(path = "/obtenerXId/{id}")
    ResponseEntity<UtensilioDTO> obtenerPorId(@PathVariable Integer id);

    @PostMapping(path = "/eliminarInventario/{id}")
    ResponseEntity<String> eliminarInventario(@PathVariable(required = true)  Integer id);

    @PostMapping(path = "/editarStock")
    ResponseEntity<String> editarStockMM(@RequestBody(required = true) Map<String,String> objetoMap);

    @GetMapping(path = "/listarAlmacenesPorIdUtensilio/{id}")
    ResponseEntity<List<EstacionDTO>> listarAlmacenesPorIdUtensilio(@PathVariable Integer id);

    @PostMapping(path = "/agregarInventario")
    ResponseEntity<String> agregarInventario(@RequestBody(required = true) Map<String,String> objetoMap);

}
