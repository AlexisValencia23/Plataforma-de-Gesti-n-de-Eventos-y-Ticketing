package com.tuapp.msreservas.controller;

import com.tuapp.msreservas.dto.*;
import com.tuapp.msreservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Reservas temporales de cupos para eventos, con expiración automática")
public class ReservaController {

    private static final Logger log = LoggerFactory.getLogger(ReservaController.class);
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(
            summary = "Listar todas las reservas",
            description = "Retorna todas las reservas registradas en el sistema, sin filtrar por estado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        log.info("GET /api/reservas");
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @Operation(
            summary = "Obtener reserva por ID",
            description = "Busca y retorna una reserva específica según su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtener(
            @Parameter(description = "ID de la reserva a buscar", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/reservas/{}", id);
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(
            summary = "Listar reservas por usuario",
            description = "Retorna todas las reservas realizadas por un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas del usuario obtenida correctamente")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long usuarioId) {
        log.info("GET /api/reservas/usuario/{}", usuarioId);
        return ResponseEntity.ok(reservaService.listarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Listar reservas por evento",
            description = "Retorna todas las reservas realizadas para un evento específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas del evento obtenida correctamente")
    })
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorEvento(
            @Parameter(description = "ID del evento", example = "1")
            @PathVariable Long eventoId) {
        log.info("GET /api/reservas/evento/{}", eventoId);
        return ResponseEntity.ok(reservaService.listarPorEvento(eventoId));
    }

    @Operation(
            summary = "Crear una nueva reserva",
            description = "Crea una reserva temporal validando que el usuario exista y que el evento tenga disponibilidad. " +
                    "La reserva expira automáticamente 15 minutos después de su creación si no es confirmada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente, expira en 15 minutos"),
            @ApiResponse(responseCode = "400", description = "Usuario inexistente o evento sin disponibilidad", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaRequestDTO dto) {
        log.info("POST /api/reservas");
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(dto));
    }

    @Operation(
            summary = "Confirmar una reserva",
            description = "Cambia el estado de la reserva de PENDIENTE a CONFIRMADA. " +
                    "Si la reserva ya expiró (han pasado más de 15 minutos), se cancela automáticamente y se rechaza la confirmación."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva confirmada correctamente"),
            @ApiResponse(responseCode = "400", description = "La reserva no está en estado PENDIENTE o ya expiró", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponseDTO> confirmar(
            @Parameter(description = "ID de la reserva a confirmar", example = "1")
            @PathVariable Long id) {
        log.info("PATCH /api/reservas/{}/confirmar", id);
        return ResponseEntity.ok(reservaService.confirmar(id));
    }

    @Operation(
            summary = "Cancelar una reserva",
            description = "Cambia el estado de la reserva a CANCELADA. No se puede cancelar una reserva que ya está cancelada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada correctamente"),
            @ApiResponse(responseCode = "400", description = "La reserva ya estaba cancelada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(
            @Parameter(description = "ID de la reserva a cancelar", example = "1")
            @PathVariable Long id) {
        log.info("PATCH /api/reservas/{}/cancelar", id);
        return ResponseEntity.ok(reservaService.cancelar(id));
    }
}