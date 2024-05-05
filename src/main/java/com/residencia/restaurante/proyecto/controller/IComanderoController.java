package com.residencia.restaurante.proyecto.controller;

import com.residencia.restaurante.proyecto.dto.ProductoDto;
import com.residencia.restaurante.proyecto.entity.Orden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/comandero")
public interface IComanderoController {
    @PostMapping(path = "/abrirOrden")
    ResponseEntity<Orden> abrirOrden(@RequestBody(required = true) Map<String, String> objetoMap);

    @PostMapping(path = "/asignarPlatillos")
    ResponseEntity<String> asignarPlatillos(@RequestBody(required = true) Map<String, String> objetoMap);

    @GetMapping(path = "/obtenerProductos")
    ResponseEntity<List<ProductoDto>> obtenerProductos();

    @GetMapping(path = "/obtenerProducto")
    ResponseEntity<ProductoDto> obtenerProducto(@RequestBody(required = true) Map<String, String> objetoMap);

    @GetMapping(path = "/obtenerOrdenes")
    ResponseEntity<List<Orden>> obtenerOrdenes();




}
