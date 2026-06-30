package com.tuapp.mspagos.controller;

import com.tuapp.mspagos.dto.*;
import com.tuapp.mspagos.service.PagoService;
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
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Procesamiento y gestión de pagos de tickets")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(
            summary = "Listar todos los pagos",
            description = "Retorna todos los pagos registrados en el sistema, sin filtrar por estado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        log.info("GET /api/pagos");
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @Operation(
            summary = "Obtener pago por ID",
            description = "Busca y retorna un pago específico según su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtener(
            @Parameter(description = "ID del pago a buscar", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/pagos/{}", id);
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @Operation(
            summary = "Listar pagos por usuario",
            description = "Retorna todos los pagos realizados por un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagos del usuario obtenida correctamente")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long usuarioId) {
        log.info("GET /api/pagos/usuario/{}", usuarioId);
        return ResponseEntity.ok(pagoService.listarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Procesar un nuevo pago",
            description = "Crea un pago validando que el usuario exista, que el ticket esté en estado PENDIENTE, " +
                    "que el monto coincida con el precio total del ticket y que el método de pago sea válido " +
                    "(TARJETA, TRANSFERENCIA o EFECTIVO). Al completarse, confirma automáticamente el ticket en ms-tickets."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago procesado y ticket confirmado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Usuario inexistente, ticket no pendiente, monto incorrecto o método de pago inválido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PagoResponseDTO> crear(@Valid @RequestBody PagoRequestDTO dto) {
        log.info("POST /api/pagos");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crear(dto));
    }

    @Operation(
            summary = "Reembolsar un pago",
            description = "Cambia el estado del pago a REEMBOLSADO y cancela automáticamente el ticket asociado en ms-tickets. " +
                    "Solo se pueden reembolsar pagos en estado COMPLETADO."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago reembolsado y ticket cancelado correctamente"),
            @ApiResponse(responseCode = "400", description = "El pago no está en estado COMPLETADO", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    @PatchMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponseDTO> reembolsar(
            @Parameter(description = "ID del pago a reembolsar", example = "1")
            @PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/reembolsar", id);
        return ResponseEntity.ok(pagoService.reembolsar(id));
    }
}