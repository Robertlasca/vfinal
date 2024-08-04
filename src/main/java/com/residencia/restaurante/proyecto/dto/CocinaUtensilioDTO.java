package com.residencia.restaurante.proyecto.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class CocinaUtensilioDTO {
    Integer id;
    int cantidad;
    String nombreCocina;
}
