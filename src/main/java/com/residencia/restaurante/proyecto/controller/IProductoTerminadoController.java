package com.residencia.restaurante.proyecto.controller;

import com.residencia.restaurante.proyecto.dto.IngredienteProductoTerminado;
import com.residencia.restaurante.proyecto.dto.ProductoTerminadoDto;
import com.residencia.restaurante.proyecto.dto.RecetaDTO;
import com.residencia.restaurante.proyecto.entity.ProductoTerminado;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(path = "/productoTerminado")
public interface IProductoTerminadoController {

    @GetMapping(path = "/obtenerActivos")
    ResponseEntity<List<ProductoTerminadoDto>> obtenerActivos();

    @GetMapping(path = "/obtenerNoActivos")
    ResponseEntity<List<ProductoTerminadoDto>> obtenerNoActivos();

    @GetMapping(path = "/obtener")
    ResponseEntity<List<ProductoTerminadoDto>> obtenerTodos();

    @PostMapping(path = "/agregar")
    ResponseEntity<String> agregar(@RequestParam("nombre") String nombre,
                                   @RequestParam("unidadMedida") String unidadMedida,
                                   @RequestParam("descripcion") String descripcion,
                                   @RequestParam("stockMax") double stockMax,
                                   @RequestParam("stockMin") double stockMin,
                                   @RequestParam("img") MultipartFile file,
                                          @RequestParam("idCategoria") int idCategoria,
                                          @RequestPart("materias")String materias
                                          );

    @GetMapping(path = "/obtenerMateriaPrima/{id}")
    ResponseEntity<IngredienteProductoTerminado> obtenerMateriaPrimaId(@PathVariable Integer id);

    @GetMapping(path = "/obtenerProductoTerminado/{id}")
    ResponseEntity<ProductoTerminado> obtenerProductoTerminado(@PathVariable Integer id);

    @GetMapping(path = "/obtenerReceta/{id}")
    ResponseEntity<List<RecetaDTO>> obtenerRecetaId(@PathVariable Integer id);




}
