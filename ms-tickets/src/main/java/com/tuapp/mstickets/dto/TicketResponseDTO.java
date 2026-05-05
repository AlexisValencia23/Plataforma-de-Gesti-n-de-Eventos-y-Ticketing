package com.tuapp.mstickets.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long eventoId;
    private Integer cantidad;
    private Double precioUnitario;
    private Double precioTotal;
    private String estado;
    private LocalDateTime creadoEn;
}