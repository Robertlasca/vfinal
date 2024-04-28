package com.residencia.restaurante.proyecto.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IComanderoService {
    ResponseEntity<String> abrirOrden(Map<String, String> objetoMap);

    ResponseEntity<String> asignarPlatillos(Map<String, String> objetoMap);
}
