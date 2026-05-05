package com.tuapp.mspagos.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {
    private Long id;
    private Long ticketId;
    private Long usuarioId;
    private Double monto;
    private String metodoPago;
    private String estado;
    private String referencia;
    private LocalDateTime creadoEn;
}