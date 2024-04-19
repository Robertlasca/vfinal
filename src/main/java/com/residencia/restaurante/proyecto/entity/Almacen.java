package com.residencia.restaurante.proyecto.entity;

import jakarta.persistence.*;
import lombok.*;
/**
 * Representa un almacén en el sistema del restaurante.
 * Contiene información sobre el nombre, la descripción, la visibilidad y la cocina asociada al almacén.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Table(name="almacen")
@Entity
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(length = 30)
    private String nombre;
    @Column(length = 50)
    private  String descripcion;

    private boolean visibilidad=true;

    @OneToOne
    @JoinColumn(nullable = false,name = "cocina_id")
    private Cocina cocina;



}
