package com.tuapp.msdescuentos.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescuentoResponseDTO {
    private Long id;
    private String codigo;
    private String descripcion;
    private Double porcentaje;
    private Integer usoMaximo;
    private Integer usoActual;
    private Boolean activo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime creadoEn;
}