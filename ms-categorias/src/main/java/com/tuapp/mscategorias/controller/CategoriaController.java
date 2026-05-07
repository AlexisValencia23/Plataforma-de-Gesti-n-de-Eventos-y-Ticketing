package com.tuapp.mscategorias.controller;

import com.tuapp.mscategorias.dto.*;
import com.tuapp.mscategorias.service.CategoriaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarActivas() {
        log.info("GET /api/categorias");
        return ResponseEntity.ok(categoriaService.listarActivas());
    }

    @GetMapping("/todas")
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        log.info("GET /api/categorias/todas");
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/categorias/{}", id);
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("POST /api/categorias");
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("PUT /api/categorias/{}", id);
        return ResponseEntity.ok(categoriaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/categorias/{}", id);
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}