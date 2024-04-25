package com.residencia.restaurante.proyecto.wrapper;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class ReceraWrapper {
    private Integer id;
    private double cantidad;
    private String esIngrediente;
}
