package com.residencia.restaurante.proyecto.dto;

import com.residencia.restaurante.proyecto.entity.MateriaPrima;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class MateriaPrimaDTO {
    MateriaPrima materiaPrimaDTO;
    String estado;
}
