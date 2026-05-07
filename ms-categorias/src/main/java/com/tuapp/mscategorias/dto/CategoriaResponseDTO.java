package com.tuapp.mscategorias.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}