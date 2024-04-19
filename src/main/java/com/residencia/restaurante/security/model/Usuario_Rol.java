package com.residencia.restaurante.security.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="usuario_rol")
@Entity
public class Usuario_Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false,name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(nullable = false,name = "rol_id")
    private Rol rol;

    private LocalDate fechaAsignacion;

}
