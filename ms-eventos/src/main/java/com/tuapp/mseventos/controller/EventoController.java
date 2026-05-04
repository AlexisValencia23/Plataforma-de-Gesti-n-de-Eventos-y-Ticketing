package com.tuapp.mseventos.controller;

import com.tuapp.mseventos.dto.*;
import com.tuapp.mseventos.service.EventoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private static final Logger log = LoggerFactory.getLogger(EventoController.class);
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listarActivos() {
        log.info("GET /api/eventos");
        return ResponseEntity.ok(eventoService.listarActivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        log.info("GET /api/eventos/todos");
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/eventos/{}", id);
        return ResponseEntity.ok(eventoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<EventoResponseDTO> crear(@Valid @RequestBody EventoRequestDTO dto) {
        log.info("POST /api/eventos");
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody EventoRequestDTO dto) {
        log.info("PUT /api/eventos/{}", id);
        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EventoResponseDTO> cancelar(@PathVariable Long id) {
        log.info("PATCH /api/eventos/{}/cancelar", id);
        return ResponseEntity.ok(eventoService.cancelar(id));
    }

    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<Boolean> verificarDisponibilidad(@PathVariable Long id,
                                                           @RequestParam Integer cantidad) {
        log.info("GET /api/eventos/{}/disponibilidad", id);
        return ResponseEntity.ok(eventoService.verificarDisponibilidad(id, cantidad));
    }

    @PatchMapping("/{id}/reducir-capacidad")
    public ResponseEntity<Void> reducirCapacidad(@PathVariable Long id,
                                                 @RequestParam Integer cantidad) {
        log.info("PATCH /api/eventos/{}/reducir-capacidad", id);
        eventoService.reducirCapacidad(id, cantidad);
        return ResponseEntity.noContent().build();
    }
}