package com.residencia.restaurante.proyecto.service;

import com.residencia.restaurante.proyecto.dto.DetalleVentaDTO;
import com.residencia.restaurante.proyecto.dto.VentasDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IVentasService {
    ResponseEntity<List<VentasDTO>> obtenerTodas();

    ResponseEntity<DetalleVentaDTO> obtenerDetalleVenta(Integer id);
}
