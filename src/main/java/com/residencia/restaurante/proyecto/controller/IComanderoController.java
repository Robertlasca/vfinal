package com.residencia.restaurante.proyecto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/comandero")
public interface IComanderoController {
    @PostMapping(path = "/abrirOrden")
    ResponseEntity<String> abrirOrden(@RequestBody(required = true) Map<String, String> objetoMap);

    @PostMapping(path = "/asignarPlatillos")
    ResponseEntity<String> asignarPlatillos(@RequestBody(required = true) Map<String, String> objetoMap);


}
