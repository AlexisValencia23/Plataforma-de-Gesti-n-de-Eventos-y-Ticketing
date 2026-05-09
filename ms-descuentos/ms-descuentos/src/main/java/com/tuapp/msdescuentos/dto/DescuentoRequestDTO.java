package com.tuapp.msdescuentos.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoRequestDTO {

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El porcentaje minimo es 1")
    @Max(value = 100, message = "El porcentaje maximo es 100")
    private Double porcentaje;

    @NotNull(message = "El uso maximo es obligatorio")
    @Min(value = 1, message = "El uso maximo debe ser al menos 1")
    private Integer usoMaximo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;
}