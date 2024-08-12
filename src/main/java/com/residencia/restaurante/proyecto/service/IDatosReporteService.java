package com.residencia.restaurante.proyecto.service;

import com.residencia.restaurante.proyecto.dto.TotalVentasDTO;
import com.residencia.restaurante.proyecto.dto.VentasDTO;
import com.residencia.restaurante.proyecto.entity.Inventario;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IDatosReporteService {
    ResponseEntity<List<Inventario>> obtenerInventarioAgotadoXAlmacen(Integer id);

    ResponseEntity<TotalVentasDTO> obtenerDatosVentasXDia(String dia);

    ResponseEntity<TotalVentasDTO> obtenerDatosVentasXMes(String diaInicio, String diaFin);

    ResponseEntity<TotalVentasDTO> obterDatosVentasMes(String mes);
}
