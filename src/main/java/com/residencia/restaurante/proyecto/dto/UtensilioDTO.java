package com.residencia.restaurante.proyecto.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class UtensilioDTO {
    Integer id;
    String descripcion;
    String nombre;
    List<CocinaUtensilioDTO> cocinaUtensilioDTOS;

}
