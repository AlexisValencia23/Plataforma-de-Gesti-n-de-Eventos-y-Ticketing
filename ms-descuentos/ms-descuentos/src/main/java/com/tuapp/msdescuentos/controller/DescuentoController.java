package com.tuapp.msdescuentos.controller;

import com.tuapp.msdescuentos.dto.*;
import com.tuapp.msdescuentos.service.DescuentoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/descuentos")
public class DescuentoController {

    private static final Logger log = LoggerFactory.getLogger(DescuentoController.class);
    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService) {
        this.descuentoService = descuentoService;
    }

    @GetMapping
    public ResponseEntity<List<DescuentoResponseDTO>> listarActivos() {
        log.info("GET /api/descuentos");
        return ResponseEntity.ok(descuentoService.listarActivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<DescuentoResponseDTO>> listarTodos() {
        log.info("GET /api/descuentos/todos");
        return ResponseEntity.ok(descuentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DescuentoResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/descuentos/{}", id);
        return ResponseEntity.ok(descuentoService.obtenerPorId(id));
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<DescuentoResponseDTO> validar(@PathVariable String codigo) {
        log.info("GET /api/descuentos/validar/{}", codigo);
        return ResponseEntity.ok(descuentoService.validar(codigo));
    }

    @PostMapping
    public ResponseEntity<DescuentoResponseDTO> crear(@Valid @RequestBody DescuentoRequestDTO dto) {
        log.info("POST /api/descuentos");
        return ResponseEntity.status(HttpStatus.CREATED).body(descuentoService.crear(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        log.info("DELETE /api/descuentos/{}", id);
        descuentoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}