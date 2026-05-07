package com.tuapp.mslugares.controller;

import com.tuapp.mslugares.dto.*;
import com.tuapp.mslugares.service.LugarService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/lugares")
public class LugarController {

    private static final Logger log = LoggerFactory.getLogger(LugarController.class);
    private final LugarService lugarService;

    public LugarController(LugarService lugarService) {
        this.lugarService = lugarService;
    }

    @GetMapping
    public ResponseEntity<List<LugarResponseDTO>> listar() {
        log.info("GET /api/lugares");
        return ResponseEntity.ok(lugarService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LugarResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/lugares/{}", id);
        return ResponseEntity.ok(lugarService.obtenerPorId(id));
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<LugarResponseDTO>> listarPorCiudad(@PathVariable String ciudad) {
        log.info("GET /api/lugares/ciudad/{}", ciudad);
        return ResponseEntity.ok(lugarService.listarPorCiudad(ciudad));
    }

    @GetMapping("/capacidad/{capacidad}")
    public ResponseEntity<List<LugarResponseDTO>> listarPorCapacidad(@PathVariable Integer capacidad) {
        log.info("GET /api/lugares/capacidad/{}", capacidad);
        return ResponseEntity.ok(lugarService.listarPorCapacidad(capacidad));
    }

    @PostMapping
    public ResponseEntity<LugarResponseDTO> crear(@Valid @RequestBody LugarRequestDTO dto) {
        log.info("POST /api/lugares");
        return ResponseEntity.status(HttpStatus.CREATED).body(lugarService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LugarResponseDTO> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody LugarRequestDTO dto) {
        log.info("PUT /api/lugares/{}", id);
        return ResponseEntity.ok(lugarService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/lugares/{}", id);
        lugarService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}