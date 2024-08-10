package com.residencia.restaurante.proyecto.controller;

import com.residencia.restaurante.proyecto.dto.AlmacenDTO;
import com.residencia.restaurante.proyecto.dto.TotalVentasDTO;
import com.residencia.restaurante.proyecto.dto.VentasDTO;
import com.residencia.restaurante.proyecto.entity.Inventario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/datos")
public interface IDatosReporteController {
    @GetMapping(path = "/inventarioAgotadoXAlmace/{id}")
    ResponseEntity<List<Inventario>> obtenerInventarioAgotadoXAlmacen(@PathVariable Integer id);


    @GetMapping(path = "/ventasXDia")
    ResponseEntity<TotalVentasDTO> ventasXDia(@RequestParam(required = true) String dia);
}
