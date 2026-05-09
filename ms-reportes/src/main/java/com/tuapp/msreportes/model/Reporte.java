package com.tuapp.msreportes.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo; // "VENTAS", "EVENTOS", "USUARIOS", "TICKETS"

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String estado; // "GENERANDO", "COMPLETADO", "FALLIDO"

    @Column(nullable = false)
    private Long generadoPor; // usuarioId

    @Column(length = 5000)
    private String contenido;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "completado_en")
    private LocalDateTime completadoEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        if (this.estado == null) this.estado = "GENERANDO";
    }
}