package com.residencia.restaurante.proyecto.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class ArqueoSaldosDTO {
    private Integer id;
    private String medioPago;
    private  double saldoAnotado;
}
