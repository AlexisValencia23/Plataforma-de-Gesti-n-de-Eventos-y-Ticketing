package com.tuapp.mstickets.controller;

import com.tuapp.mstickets.dto.*;
import com.tuapp.mstickets.service.TicketService;
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
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Compra y gestión de tickets de eventos")
public class TicketController {

    private static final Logger log = LoggerFactory.getLogger(TicketController.class);
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(
            summary = "Listar todos los tickets",
            description = "Retorna todos los tickets registrados en el sistema, sin filtrar por estado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> listarTodos() {
        log.info("GET /api/tickets");
        return ResponseEntity.ok(ticketService.listarTodos());
    }

    @Operation(
            summary = "Obtener ticket por ID",
            description = "Busca y retorna un ticket específico según su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> obtener(
            @Parameter(description = "ID del ticket a buscar", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/tickets/{}", id);
        return ResponseEntity.ok(ticketService.obtenerPorId(id));
    }

    @Operation(
            summary = "Listar tickets por usuario",
            description = "Retorna todos los tickets asociados a un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets del usuario obtenida correctamente")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TicketResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long usuarioId) {
        log.info("GET /api/tickets/usuario/{}", usuarioId);
        return ResponseEntity.ok(ticketService.listarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Listar tickets por evento",
            description = "Retorna todos los tickets vendidos para un evento específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets del evento obtenida correctamente")
    })
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<TicketResponseDTO>> listarPorEvento(
            @Parameter(description = "ID del evento", example = "1")
            @PathVariable Long eventoId) {
        log.info("GET /api/tickets/evento/{}", eventoId);
        return ResponseEntity.ok(ticketService.listarPorEvento(eventoId));
    }

    @Operation(
            summary = "Crear un nuevo ticket",
            description = "Crea un ticket validando que el usuario exista en ms-usuarios y que el evento tenga disponibilidad en ms-eventos. " +
                    "Obtiene el precio desde ms-eventos y reduce la capacidad disponible del evento al confirmarse la compra."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Usuario inexistente o evento sin disponibilidad", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TicketResponseDTO> crear(@Valid @RequestBody TicketRequestDTO dto) {
        log.info("POST /api/tickets");
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.crear(dto));
    }

    @Operation(
            summary = "Confirmar un ticket",
            description = "Cambia el estado del ticket de PENDIENTE a CONFIRMADO. Utilizado por ms-pagos al completar un pago exitoso."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket confirmado correctamente"),
            @ApiResponse(responseCode = "400", description = "El ticket no está en estado PENDIENTE", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content)
    })
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<TicketResponseDTO> confirmar(
            @Parameter(description = "ID del ticket a confirmar", example = "1")
            @PathVariable Long id) {
        log.info("PATCH /api/tickets/{}/confirmar", id);
        return ResponseEntity.ok(ticketService.confirmar(id));
    }

    @Operation(
            summary = "Cancelar un ticket",
            description = "Cambia el estado del ticket a CANCELADO. No se puede cancelar un ticket que ya está cancelado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket cancelado correctamente"),
            @ApiResponse(responseCode = "400", description = "El ticket ya estaba cancelado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content)
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<TicketResponseDTO> cancelar(
            @Parameter(description = "ID del ticket a cancelar", example = "1")
            @PathVariable Long id) {
        log.info("PATCH /api/tickets/{}/cancelar", id);
        return ResponseEntity.ok(ticketService.cancelar(id));
    }
}