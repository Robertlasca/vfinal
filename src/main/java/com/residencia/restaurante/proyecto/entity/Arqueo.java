package com.residencia.restaurante.proyecto.entity;

import com.residencia.restaurante.security.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
/**
 * Representa el arqueo de una caja en un momento específico, incluyendo información sobre el usuario que realizó el arqueo,
 * la caja asociada, los saldos inicial y final, así como el estado del arqueo.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Table(name="arqueo")
@Entity
public class Arqueo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false,name = "caja_id")
    private Caja caja;

    @ManyToOne
    @JoinColumn(nullable = false,name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime fechaHoraApertura=LocalDateTime.now();

    private double saldoInicial;

    private double saldoFinal;

    private double saldoMaximo;

    private double saldoIngresado;

    private LocalDateTime fechaHoraCierre;

    private String comentario="Sin comentarios.";

    private boolean estadoArqueo=true;

}
