package com.residencia.restaurante.proyecto.dto;

import com.residencia.restaurante.proyecto.entity.Arqueo;
import com.residencia.restaurante.proyecto.entity.Venta;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArqueoDTO {
    Arqueo arqueo;
    double gastos;
    double ingresos;
    String estado;
    List<VentaMedioPagoDTO> ventaMedioPagoDTOS;
    List<ArqueoSaldosDTO> arqueoSaldosDTOS;
}
