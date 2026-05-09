package com.tuapp.msnotificaciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String tipo; // "CONFIRMACION_TICKET", "CANCELACION", "RECORDATORIO", "PAGO"

    @Column(nullable = false)
    private String mensaje;

    @Column(nullable = false)
    private String canal; // "EMAIL", "SMS", "SISTEMA"

    @Column(nullable = false)
    private String estado; // "PENDIENTE", "ENVIADA", "FALLIDA"

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "enviado_en")
    private LocalDateTime enviadoEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        if (this.estado == null) this.estado = "PENDIENTE";
    }
}