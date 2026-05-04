package com.tuapp.mseventos.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String lugar;
    private Integer capacidadTotal;
    private Integer capacidadDisponible;
    private Double precio;
    private String categoria;
    private String estado;
    private Long organizadorId;
    private LocalDateTime creadoEn;
}