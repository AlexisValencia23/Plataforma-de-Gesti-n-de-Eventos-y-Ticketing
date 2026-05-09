package com.tuapp.msreservas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long eventoId;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private String estado; // "PENDIENTE", "CONFIRMADA", "CANCELADA"

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "expira_en")
    private LocalDateTime expiraEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        if (this.estado == null) this.estado = "PENDIENTE";
        this.expiraEn = LocalDateTime.now().plusMinutes(15);
    }
}