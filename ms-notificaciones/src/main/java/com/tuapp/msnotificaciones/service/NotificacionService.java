package com.tuapp.msnotificaciones.service;

import com.tuapp.msnotificaciones.dto.*;
import com.tuapp.msnotificaciones.model.Notificacion;
import com.tuapp.msnotificaciones.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);
    private final NotificacionRepository notificacionRepository;
    private final WebClient webClientUsuarios;

    public NotificacionService(NotificacionRepository notificacionRepository,
                               WebClient.Builder webClientBuilder) {
        this.notificacionRepository = notificacionRepository;
        this.webClientUsuarios = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public List<NotificacionResponseDTO> listarTodas() {
        log.info("Listando todas las notificaciones");
        return notificacionRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando notificaciones del usuario: {}", usuarioId);
        return notificacionRepository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    public List<NotificacionResponseDTO> listarPorEstado(String estado) {
        log.info("Listando notificaciones con estado: {}", estado);
        return notificacionRepository.findByEstado(estado).stream().map(this::toResponse).toList();
    }

    public NotificacionResponseDTO obtenerPorId(Long id) {
        log.info("Buscando notificacion con id: {}", id);
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notificacion no encontrada con id: " + id));
        return toResponse(notificacion);
    }

    public NotificacionResponseDTO crear(NotificacionRequestDTO dto) {
        log.info("Creando notificacion para usuario: {}", dto.getUsuarioId());

        // Verificar que el usuario existe
        Boolean usuarioExiste = webClientUsuarios.get()
                .uri("/api/usuarios/{id}/existe", dto.getUsuarioId())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (usuarioExiste == null || !usuarioExiste) {
            log.warn("Usuario {} no existe o esta inactivo", dto.getUsuarioId());
            throw new RuntimeException("El usuario no existe o esta inactivo");
        }

        // Validar canal
        List<String> canales = List.of("EMAIL", "SMS", "SISTEMA");
        if (!canales.contains(dto.getCanal().toUpperCase())) {
            throw new RuntimeException("Canal invalido. Use: EMAIL, SMS o SISTEMA");
        }

        // Validar tipo
        List<String> tipos = List.of("CONFIRMACION_TICKET", "CANCELACION", "RECORDATORIO", "PAGO");
        if (!tipos.contains(dto.getTipo().toUpperCase())) {
            throw new RuntimeException("Tipo invalido. Use: CONFIRMACION_TICKET, CANCELACION, RECORDATORIO o PAGO");
        }

        Notificacion notificacion = Notificacion.builder()
                .usuarioId(dto.getUsuarioId())
                .tipo(dto.getTipo().toUpperCase())
                .mensaje(dto.getMensaje())
                .canal(dto.getCanal().toUpperCase())
                .build();

        Notificacion guardada = notificacionRepository.save(notificacion);
        log.info("Notificacion creada con id: {}", guardada.getId());

        // Simular envio
        guardada.setEstado("ENVIADA");
        guardada.setEnviadoEn(LocalDateTime.now());
        notificacionRepository.save(guardada);
        log.info("Notificacion enviada correctamente via {}", guardada.getCanal());

        return toResponse(guardada);
    }

    public NotificacionResponseDTO reenviar(Long id) {
        log.info("Reenviando notificacion con id: {}", id);
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notificacion no encontrada con id: " + id));

        notificacion.setEstado("ENVIADA");
        notificacion.setEnviadoEn(LocalDateTime.now());
        notificacionRepository.save(notificacion);
        log.info("Notificacion reenviada correctamente");
        return toResponse(notificacion);
    }

    private NotificacionResponseDTO toResponse(Notificacion n) {
        return NotificacionResponseDTO.builder()
                .id(n.getId())
                .usuarioId(n.getUsuarioId())
                .tipo(n.getTipo())
                .mensaje(n.getMensaje())
                .canal(n.getCanal())
                .estado(n.getEstado())
                .creadoEn(n.getCreadoEn())
                .enviadoEn(n.getEnviadoEn())
                .build();
    }
}