package com.tuapp.msreportes.service;

import com.tuapp.msreportes.dto.*;
import com.tuapp.msreportes.model.Reporte;
import com.tuapp.msreportes.repository.ReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);
    private final ReporteRepository reporteRepository;
    private final WebClient webClientUsuarios;

    public ReporteService(ReporteRepository reporteRepository, WebClient.Builder webClientBuilder) {
        this.reporteRepository = reporteRepository;
        this.webClientUsuarios = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public List<ReporteResponseDTO> listarTodos() {
        log.info("Listando todos los reportes");
        return reporteRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ReporteResponseDTO> listarPorTipo(String tipo) {
        log.info("Listando reportes de tipo: {}", tipo);
        return reporteRepository.findByTipo(tipo.toUpperCase()).stream().map(this::toResponse).toList();
    }

    public List<ReporteResponseDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando reportes generados por usuario: {}", usuarioId);
        return reporteRepository.findByGeneradoPor(usuarioId).stream().map(this::toResponse).toList();
    }

    public ReporteResponseDTO obtenerPorId(Long id) {
        log.info("Buscando reporte con id: {}", id);
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reporte no encontrado con id: " + id));
        return toResponse(reporte);
    }

    public ReporteResponseDTO generar(ReporteRequestDTO dto) {
        log.info("Generando reporte de tipo: {} por usuario: {}", dto.getTipo(), dto.getGeneradoPor());

        // Validar tipo
        List<String> tipos = List.of("VENTAS", "EVENTOS", "USUARIOS", "TICKETS");
        if (!tipos.contains(dto.getTipo().toUpperCase())) {
            throw new RuntimeException("Tipo invalido. Use: VENTAS, EVENTOS, USUARIOS o TICKETS");
        }

        // Verificar que el usuario existe
        Boolean usuarioExiste = webClientUsuarios.get()
                .uri("/api/usuarios/{id}/existe", dto.getGeneradoPor())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (usuarioExiste == null || !usuarioExiste) {
            log.warn("Usuario {} no existe o esta inactivo", dto.getGeneradoPor());
            throw new RuntimeException("El usuario no existe o esta inactivo");
        }

        Reporte reporte = Reporte.builder()
                .tipo(dto.getTipo().toUpperCase())
                .descripcion(dto.getDescripcion())
                .generadoPor(dto.getGeneradoPor())
                .build();

        Reporte guardado = reporteRepository.save(reporte);

        // Simular generacion del reporte
        String contenido = generarContenido(dto.getTipo().toUpperCase());
        guardado.setContenido(contenido);
        guardado.setEstado("COMPLETADO");
        guardado.setCompletadoEn(LocalDateTime.now());
        reporteRepository.save(guardado);

        log.info("Reporte generado correctamente con id: {}", guardado.getId());
        return toResponse(guardado);
    }

    private String generarContenido(String tipo) {
        return switch (tipo) {
            case "VENTAS" -> "Reporte de ventas: Total tickets vendidos, ingresos por evento, metodos de pago utilizados.";
            case "EVENTOS" -> "Reporte de eventos: Eventos activos, cancelados, agotados y finalizados.";
            case "USUARIOS" -> "Reporte de usuarios: Total usuarios registrados, activos e inactivos.";
            case "TICKETS" -> "Reporte de tickets: Tickets pendientes, confirmados y cancelados por evento.";
            default -> "Reporte generado correctamente.";
        };
    }

    private ReporteResponseDTO toResponse(Reporte r) {
        return ReporteResponseDTO.builder()
                .id(r.getId())
                .tipo(r.getTipo())
                .descripcion(r.getDescripcion())
                .estado(r.getEstado())
                .generadoPor(r.getGeneradoPor())
                .contenido(r.getContenido())
                .creadoEn(r.getCreadoEn())
                .completadoEn(r.getCompletadoEn())
                .build();
    }
}