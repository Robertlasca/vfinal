package com.residencia.restaurante.proyecto.service;

import com.residencia.restaurante.proyecto.dto.ProductoDto;
import com.residencia.restaurante.proyecto.entity.Orden;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IComanderoService {
    ResponseEntity<Orden> abrirOrden(Map<String, String> objetoMap);

    ResponseEntity<String> asignarPlatillos(Map<String, String> objetoMap);

    ResponseEntity<List<ProductoDto>> obtenerProductos();

    ResponseEntity<List<Orden>> obtenerOrdenes();

    ResponseEntity<ProductoDto> obtenerProducto(Map<String, String> objetoMap);
}
