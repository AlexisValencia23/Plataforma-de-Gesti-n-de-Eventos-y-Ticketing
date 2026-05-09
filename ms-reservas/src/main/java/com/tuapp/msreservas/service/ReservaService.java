package com.tuapp.msreservas.service;

import com.tuapp.msreservas.dto.*;
import com.tuapp.msreservas.model.Reserva;
import com.tuapp.msreservas.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
public class ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);
    private final ReservaRepository reservaRepository;
    private final WebClient webClientUsuarios;
    private final WebClient webClientEventos;

    public ReservaService(ReservaRepository reservaRepository, WebClient.Builder webClientBuilder) {
        this.reservaRepository = reservaRepository;
        this.webClientUsuarios = webClientBuilder.baseUrl("http://localhost:8081").build();
        this.webClientEventos = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public List<ReservaResponseDTO> listarTodas() {
        log.info("Listando todas las reservas");
        return reservaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ReservaResponseDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando reservas del usuario: {}", usuarioId);
        return reservaRepository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    public List<ReservaResponseDTO> listarPorEvento(Long eventoId) {
        log.info("Listando reservas del evento: {}", eventoId);
        return reservaRepository.findByEventoId(eventoId).stream().map(this::toResponse).toList();
    }

    public ReservaResponseDTO obtenerPorId(Long id) {
        log.info("Buscando reserva con id: {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reserva no encontrada con id: " + id));
        return toResponse(reserva);
    }

    public ReservaResponseDTO crear(ReservaRequestDTO dto) {
        log.info("Creando reserva para usuario: {} en evento: {}", dto.getUsuarioId(), dto.getEventoId());

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

        // Verificar disponibilidad del evento
        Boolean disponible = webClientEventos.get()
                .uri("/api/eventos/{id}/disponibilidad?cantidad={cantidad}",
                        dto.getEventoId(), dto.getCantidad())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (disponible == null || !disponible) {
            log.warn("Evento {} sin disponibilidad para {} lugares", dto.getEventoId(), dto.getCantidad());
            throw new RuntimeException("El evento no tiene disponibilidad suficiente");
        }

        Reserva reserva = Reserva.builder()
                .usuarioId(dto.getUsuarioId())
                .eventoId(dto.getEventoId())
                .cantidad(dto.getCantidad())
                .build();

        Reserva guardada = reservaRepository.save(reserva);
        log.info("Reserva creada con id: {}, expira en 15 minutos", guardada.getId());
        return toResponse(guardada);
    }

    public ReservaResponseDTO confirmar(Long id) {
        log.info("Confirmando reserva con id: {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reserva no encontrada con id: " + id));

        if (!reserva.getEstado().equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden confirmar reservas en estado PENDIENTE");
        }

        if (reserva.getExpiraEn().isBefore(java.time.LocalDateTime.now())) {
            reserva.setEstado("CANCELADA");
            reservaRepository.save(reserva);
            throw new RuntimeException("La reserva ha expirado");
        }

        reserva.setEstado("CONFIRMADA");
        return toResponse(reservaRepository.save(reserva));
    }

    public ReservaResponseDTO cancelar(Long id) {
        log.info("Cancelando reserva con id: {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reserva no encontrada con id: " + id));

        if (reserva.getEstado().equals("CANCELADA")) {
            throw new RuntimeException("La reserva ya esta cancelada");
        }

        reserva.setEstado("CANCELADA");
        return toResponse(reservaRepository.save(reserva));
    }

    private ReservaResponseDTO toResponse(Reserva r) {
        return ReservaResponseDTO.builder()
                .id(r.getId())
                .usuarioId(r.getUsuarioId())
                .eventoId(r.getEventoId())
                .cantidad(r.getCantidad())
                .estado(r.getEstado())
                .creadoEn(r.getCreadoEn())
                .expiraEn(r.getExpiraEn())
                .build();
    }
}