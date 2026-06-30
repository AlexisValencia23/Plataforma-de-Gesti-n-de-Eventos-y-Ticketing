package com.tuapp.mseventos.controller;

import com.tuapp.mseventos.dto.*;
import com.tuapp.mseventos.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/eventos")
@Tag(name = "Eventos", description = "Gestión de eventos: creación, consulta, actualización y disponibilidad")
public class EventoController {

    private static final Logger log = LoggerFactory.getLogger(EventoController.class);
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @Operation(
            summary = "Listar eventos activos",
            description = "Retorna únicamente los eventos en estado ACTIVO con capacidad disponible mayor a 0."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos activos obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = EventoResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listarActivos() {
        log.info("GET /api/eventos");
        return ResponseEntity.ok(eventoService.listarActivos());
    }

    @Operation(
            summary = "Listar todos los eventos",
            description = "Retorna todos los eventos sin filtrar por estado, incluyendo cancelados, agotados y finalizados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista completa de eventos obtenida correctamente")
    })
    @GetMapping("/todos")
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        log.info("GET /api/eventos/todos");
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @Operation(
            summary = "Obtener evento por ID",
            description = "Busca y retorna un evento específico según su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> obtener(
            @Parameter(description = "ID del evento a buscar", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/eventos/{}", id);
        return ResponseEntity.ok(eventoService.obtenerPorId(id));
    }

    @Operation(
            summary = "Crear un nuevo evento",
            description = "Crea un evento validando que el organizador exista en ms-usuarios y que las fechas sean coherentes. " +
                    "La capacidad disponible se inicializa igual a la capacidad total."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, fechas incoherentes o organizador inexistente", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EventoResponseDTO> crear(@Valid @RequestBody EventoRequestDTO dto) {
        log.info("POST /api/eventos");
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crear(dto));
    }

    @Operation(
            summary = "Actualizar un evento existente",
            description = "Actualiza los datos generales de un evento existente identificado por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> actualizar(
            @Parameter(description = "ID del evento a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EventoRequestDTO dto) {
        log.info("PUT /api/eventos/{}", id);
        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    @Operation(
            summary = "Cancelar un evento",
            description = "Cambia el estado del evento a CANCELADO. Esta acción no elimina el registro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento cancelado correctamente"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EventoResponseDTO> cancelar(
            @Parameter(description = "ID del evento a cancelar", example = "1")
            @PathVariable Long id) {
        log.info("PATCH /api/eventos/{}/cancelar", id);
        return ResponseEntity.ok(eventoService.cancelar(id));
    }

    @Operation(
            summary = "Verificar disponibilidad del evento",
            description = "Indica si el evento tiene capacidad suficiente para la cantidad solicitada y si su estado es ACTIVO. " +
                    "Utilizado por ms-tickets y ms-reservas antes de confirmar una compra o reserva."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la verificación de disponibilidad (true/false)"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @Parameter(description = "ID del evento", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Cantidad de cupos solicitados", example = "2")
            @RequestParam Integer cantidad) {
        log.info("GET /api/eventos/{}/disponibilidad", id);
        return ResponseEntity.ok(eventoService.verificarDisponibilidad(id, cantidad));
    }

    @Operation(
            summary = "Reducir la capacidad disponible del evento",
            description = "Descuenta la cantidad indicada de la capacidad disponible del evento. " +
                    "Si la capacidad llega a 0, el estado del evento cambia automáticamente a AGOTADO. " +
                    "Utilizado internamente por ms-tickets al confirmar la compra de entradas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Capacidad reducida correctamente, sin contenido de retorno"),
            @ApiResponse(responseCode = "400", description = "Capacidad insuficiente para la cantidad solicitada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @PatchMapping("/{id}/reducir-capacidad")
    public ResponseEntity<Void> reducirCapacidad(
            @Parameter(description = "ID del evento", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Cantidad a descontar de la capacidad disponible", example = "2")
            @RequestParam Integer cantidad) {
        log.info("PATCH /api/eventos/{}/reducir-capacidad", id);
        eventoService.reducirCapacidad(id, cantidad);
        return ResponseEntity.noContent().build();
    }
}