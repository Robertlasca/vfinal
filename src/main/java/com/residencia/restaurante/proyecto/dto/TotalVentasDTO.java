package com.residencia.restaurante.proyecto.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class TotalVentasDTO {
    List<VentasDTO> lista;
    String platilloMasVendido;
    double montoTotal;
}
