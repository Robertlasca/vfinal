package com.residencia.restaurante.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/productoTerminado/receta")
public interface IRecetaProductoTerminadoController {
    @PostMapping(path = "/eliminar/{id}")
    ResponseEntity<String> eliminarIngrediente(@PathVariable Integer id);

    @PostMapping(path = "/editar")
    ResponseEntity<String> editarIngredienteReceta(@RequestBody(required = true) Map<String,String> objetoMap);

    @PostMapping(path ="/agregar")
    ResponseEntity<String> agregarIngredienteReceta(@RequestBody(required = true) Map<String,String> objetoMap);

}
