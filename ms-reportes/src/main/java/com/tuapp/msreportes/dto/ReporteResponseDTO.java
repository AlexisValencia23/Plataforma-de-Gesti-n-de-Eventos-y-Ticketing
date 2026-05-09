package com.tuapp.msreportes.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteResponseDTO {
    private Long id;
    private String tipo;
    private String descripcion;
    private String estado;
    private Long generadoPor;
    private String contenido;
    private LocalDateTime creadoEn;
    private LocalDateTime completadoEn;
}