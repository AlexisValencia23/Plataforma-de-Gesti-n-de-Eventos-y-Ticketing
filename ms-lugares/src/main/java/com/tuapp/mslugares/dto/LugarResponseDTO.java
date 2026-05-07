package com.tuapp.mslugares.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LugarResponseDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String pais;
    private Integer capacidadMaxima;
    private String descripcion;
    private String telefono;
    private Boolean activo;
}