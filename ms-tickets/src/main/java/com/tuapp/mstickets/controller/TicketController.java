package com.tuapp.mstickets.controller;

import com.tuapp.mstickets.dto.*;
import com.tuapp.mstickets.service.TicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private static final Logger log = LoggerFactory.getLogger(TicketController.class);
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> listarTodos() {
        log.info("GET /api/tickets");
        return ResponseEntity.ok(ticketService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/tickets/{}", id);
        return ResponseEntity.ok(ticketService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TicketResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/tickets/usuario/{}", usuarioId);
        return ResponseEntity.ok(ticketService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<TicketResponseDTO>> listarPorEvento(@PathVariable Long eventoId) {
        log.info("GET /api/tickets/evento/{}", eventoId);
        return ResponseEntity.ok(ticketService.listarPorEvento(eventoId));
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> crear(@Valid @RequestBody TicketRequestDTO dto) {
        log.info("POST /api/tickets");
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.crear(dto));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<TicketResponseDTO> confirmar(@PathVariable Long id) {
        log.info("PATCH /api/tickets/{}/confirmar", id);
        return ResponseEntity.ok(ticketService.confirmar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<TicketResponseDTO> cancelar(@PathVariable Long id) {
        log.info("PATCH /api/tickets/{}/cancelar", id);
        return ResponseEntity.ok(ticketService.cancelar(id));
    }
}