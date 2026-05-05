package com.tuapp.mspagos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ticketId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private String metodoPago; // "TARJETA", "TRANSFERENCIA", "EFECTIVO"

    @Column(nullable = false)
    private String estado; // "PENDIENTE", "COMPLETADO", "RECHAZADO", "REEMBOLSADO"

    private String referencia;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        if (this.estado == null) this.estado = "PENDIENTE";
        this.referencia = "REF-" + System.currentTimeMillis();
    }
}