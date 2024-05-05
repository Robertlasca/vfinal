package com.residencia.restaurante.proyecto.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IRecetaProductoTerminadoService {

    ResponseEntity<String> eliminarIngrediente(Integer id);

    ResponseEntity<String> editarIngredienteReceta(Map<String, String> objetoMap);

    ResponseEntity<String> agregarIngredienteReceta(Map<String, String> objetoMap);
}
