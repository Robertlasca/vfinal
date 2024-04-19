package com.residencia.restaurante.proyecto.entity;

import jakarta.persistence.*;
import lombok.*;
/**
 * Representa una caja utilizada en el sistema del restaurante para gestionar transacciones financieras.
 * Cada caja tiene un nombre, una descripción y puede estar marcada como visible o no en el sistema.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Table(name="caja")
@Entity
public class Caja {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(length = 30)
    private  String nombre;
    @Column(length = 50)
    private String descripcion;

    private Boolean visibilidad=true;
}
