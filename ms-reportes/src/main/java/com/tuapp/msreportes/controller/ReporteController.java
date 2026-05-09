package com.tuapp.msreportes.controller;

import com.tuapp.msreportes.dto.*;
import com.tuapp.msreportes.service.ReporteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> listarTodos() {
        log.info("GET /api/reportes");
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> obtener(@PathVariable Long id) {
        log.info("GET /api/reportes/{}", id);
        return ResponseEntity.ok(reporteService.obtenerPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ReporteResponseDTO>> listarPorTipo(@PathVariable String tipo) {
        log.info("GET /api/reportes/tipo/{}", tipo);
        return ResponseEntity.ok(reporteService.listarPorTipo(tipo));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReporteResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/reportes/usuario/{}", usuarioId);
        return ResponseEntity.ok(reporteService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<ReporteResponseDTO> generar(@Valid @RequestBody ReporteRequestDTO dto) {
        log.info("POST /api/reportes");
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.generar(dto));
    }
}