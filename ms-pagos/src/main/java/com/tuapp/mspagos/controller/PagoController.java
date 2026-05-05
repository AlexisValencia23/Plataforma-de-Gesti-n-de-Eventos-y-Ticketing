package com.tuapp.mspagos.controller;

import com.tuapp.mspagos.dto.*;
import com.tuapp.mspagos.service.PagoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        log.info("GET /api/pagos");
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/pagos/{}", id);
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/pagos/usuario/{}", usuarioId);
        return ResponseEntity.ok(pagoService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<PagoResponseDTO> crear(@Valid @RequestBody PagoRequestDTO dto) {
        log.info("POST /api/pagos");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crear(dto));
    }

    @PatchMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponseDTO> reembolsar(@PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/reembolsar", id);
        return ResponseEntity.ok(pagoService.reembolsar(id));
    }
}