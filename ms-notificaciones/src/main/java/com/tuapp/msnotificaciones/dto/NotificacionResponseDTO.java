package com.tuapp.msnotificaciones.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDTO {
    private Long id;
    private Long usuarioId;
    private String tipo;
    private String mensaje;
    private String canal;
    private String estado;
    private LocalDateTime creadoEn;
    private LocalDateTime enviadoEn;
}