package com.tuapp.msnotificaciones.controller;

import com.tuapp.msnotificaciones.dto.*;
import com.tuapp.msnotificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionController.class);
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarTodas() {
        log.info("GET /api/notificaciones");
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/notificaciones/{}", id);
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/notificaciones/usuario/{}", usuarioId);
        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorEstado(@PathVariable String estado) {
        log.info("GET /api/notificaciones/estado/{}", estado);
        return ResponseEntity.ok(notificacionService.listarPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionRequestDTO dto) {
        log.info("POST /api/notificaciones");
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.crear(dto));
    }

    @PatchMapping("/{id}/reenviar")
    public ResponseEntity<NotificacionResponseDTO> reenviar(@PathVariable Long id) {
        log.info("PATCH /api/notificaciones/{}/reenviar", id);
        return ResponseEntity.ok(notificacionService.reenviar(id));
    }
}