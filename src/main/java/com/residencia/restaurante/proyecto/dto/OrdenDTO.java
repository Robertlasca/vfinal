package com.residencia.restaurante.proyecto.dto;

import com.residencia.restaurante.proyecto.entity.DetalleOrdenMenu;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class OrdenDTO {
    Integer idOrden;
    int folio;
    String nombreCliente;
    String nombreArea;
    double total;
    String estado;
    String idUsuario;
    String nombreUsuario;
    List<DetalleOrdenProductoDTO> detalleOrdenMenuList;
}
