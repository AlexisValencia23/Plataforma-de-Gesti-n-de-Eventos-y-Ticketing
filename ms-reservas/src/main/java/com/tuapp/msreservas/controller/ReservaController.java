package com.tuapp.msreservas.controller;

import com.tuapp.msreservas.dto.*;
import com.tuapp.msreservas.service.ReservaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private static final Logger log = LoggerFactory.getLogger(ReservaController.class);
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        log.info("GET /api/reservas");
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/reservas/{}", id);
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/reservas/usuario/{}", usuarioId);
        return ResponseEntity.ok(reservaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorEvento(@PathVariable Long eventoId) {
        log.info("GET /api/reservas/evento/{}", eventoId);
        return ResponseEntity.ok(reservaService.listarPorEvento(eventoId));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaRequestDTO dto) {
        log.info("POST /api/reservas");
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(dto));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponseDTO> confirmar(@PathVariable Long id) {
        log.info("PATCH /api/reservas/{}/confirmar", id);
        return ResponseEntity.ok(reservaService.confirmar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        log.info("PATCH /api/reservas/{}/cancelar", id);
        return ResponseEntity.ok(reservaService.cancelar(id));
    }
}