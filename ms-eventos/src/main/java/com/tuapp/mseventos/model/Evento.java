package com.tuapp.mseventos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private String lugar;

    @Column(nullable = false)
    private Integer capacidadTotal;

    @Column(nullable = false)
    private Integer capacidadDisponible;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String estado; // "ACTIVO", "CANCELADO", "AGOTADO", "FINALIZADO"

    @Column(nullable = false)
    private Long organizadorId; // referencia a ms-usuarios

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        if (this.estado == null) this.estado = "ACTIVO";
        this.capacidadDisponible = this.capacidadTotal;
    }
}