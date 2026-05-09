package com.tuapp.msreservas.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long eventoId;
    private Integer cantidad;
    private String estado;
    private LocalDateTime creadoEn;
    private LocalDateTime expiraEn;
}