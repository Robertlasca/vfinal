package com.residencia.restaurante.proyecto.dto;

import com.residencia.restaurante.proyecto.entity.Inventario;
import lombok.*;

/**
 * DTO (Data Transfer Object) para representar un inventario con su estado y lograr enviar el estado en un mismo e json.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class InventarioDTO {

    private Inventario inventario;

    private String estado;

}
