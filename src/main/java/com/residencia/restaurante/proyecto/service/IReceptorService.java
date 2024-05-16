package com.residencia.restaurante.proyecto.service;

import com.residencia.restaurante.proyecto.dto.ComandaDTO;
import com.residencia.restaurante.proyecto.dto.DetalleOrdenProductoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IReceptorService {
    ResponseEntity<List<DetalleOrdenProductoDTO>> obtenerComandasPorIdCocina(Integer id);

    ResponseEntity<List<DetalleOrdenProductoDTO>> obtenerComandasEnPreparacionPorIdCocina(Integer id);

    ResponseEntity<List<DetalleOrdenProductoDTO>> obtenerComandasTerminadasPorIdCocina(Integer id);

    ResponseEntity<String> cambiarPlatilloPreparacion(Integer id);

    ResponseEntity<String> cambiarPlatilloTerminado(Integer id);

    ResponseEntity<String> cambiarPlatilloCancelado(Integer id);
}
