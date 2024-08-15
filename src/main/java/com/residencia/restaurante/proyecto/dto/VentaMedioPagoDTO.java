package com.residencia.restaurante.proyecto.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VentaMedioPagoDTO {
    Integer id;
    String nombreMedio;
    double total;
}
