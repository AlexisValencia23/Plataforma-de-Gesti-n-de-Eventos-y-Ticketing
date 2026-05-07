package com.tuapp.mslugares.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lugares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String pais;

    @Column(nullable = false)
    private Integer capacidadMaxima;

    private String descripcion;

    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;
}