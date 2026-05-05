package com.tuapp.mstickets.service;

import com.tuapp.mstickets.dto.*;
import com.tuapp.mstickets.model.Ticket;
import com.tuapp.mstickets.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;
    private final WebClient webClientUsuarios;
    private final WebClient webClientEventos;

    public TicketService(TicketRepository ticketRepository, WebClient.Builder webClientBuilder) {
        this.ticketRepository = ticketRepository;
        this.webClientUsuarios = webClientBuilder.baseUrl("http://localhost:8081").build();
        this.webClientEventos = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public List<TicketResponseDTO> listarTodos() {
        log.info("Listando todos los tickets");
        return ticketRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<TicketResponseDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando tickets del usuario: {}", usuarioId);
        return ticketRepository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    public List<TicketResponseDTO> listarPorEvento(Long eventoId) {
        log.info("Listando tickets del evento: {}", eventoId);
        return ticketRepository.findByEventoId(eventoId).stream().map(this::toResponse).toList();
    }

    public TicketResponseDTO obtenerPorId(Long id) {
        log.info("Buscando ticket con id: {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket no encontrado con id: " + id));
        return toResponse(ticket);
    }

    public TicketResponseDTO crear(TicketRequestDTO dto) {
        log.info("Creando ticket para usuario: {} en evento: {}", dto.getUsuarioId(), dto.getEventoId());

        // Verificar que el usuario existe en ms-usuarios
        Boolean usuarioExiste = webClientUsuarios.get()
                .uri("/api/usuarios/{id}/existe", dto.getUsuarioId())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (usuarioExiste == null || !usuarioExiste) {
            log.warn("Usuario {} no existe o esta inactivo", dto.getUsuarioId());
            throw new RuntimeException("El usuario no existe o esta inactivo");
        }

        // Verificar disponibilidad en ms-eventos
        Boolean disponible = webClientEventos.get()
                .uri("/api/eventos/{id}/disponibilidad?cantidad={cantidad}",
                        dto.getEventoId(), dto.getCantidad())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (disponible == null || !disponible) {
            log.warn("Evento {} sin disponibilidad para {} tickets", dto.getEventoId(), dto.getCantidad());
            throw new RuntimeException("El evento no tiene disponibilidad suficiente");
        }

        // Obtener precio del evento
        Map eventoData = webClientEventos.get()
                .uri("/api/eventos/{id}", dto.getEventoId())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Double precio = ((Number) eventoData.get("precio")).doubleValue();

        // Crear el ticket
        Ticket ticket = Ticket.builder()
                .usuarioId(dto.getUsuarioId())
                .eventoId(dto.getEventoId())
                .cantidad(dto.getCantidad())
                .precioUnitario(precio)
                .build();

        Ticket guardado = ticketRepository.save(ticket);

        // Reducir capacidad en ms-eventos
        webClientEventos.patch()
                .uri("/api/eventos/{id}/reducir-capacidad?cantidad={cantidad}",
                        dto.getEventoId(), dto.getCantidad())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        log.info("Ticket creado con id: {}", guardado.getId());
        return toResponse(guardado);
    }

    public TicketResponseDTO confirmar(Long id) {
        log.info("Confirmando ticket con id: {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket no encontrado con id: " + id));
        if (!ticket.getEstado().equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden confirmar tickets en estado PENDIENTE");
        }
        ticket.setEstado("CONFIRMADO");
        return toResponse(ticketRepository.save(ticket));
    }

    public TicketResponseDTO cancelar(Long id) {
        log.info("Cancelando ticket con id: {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket no encontrado con id: " + id));
        if (ticket.getEstado().equals("CANCELADO")) {
            throw new RuntimeException("El ticket ya esta cancelado");
        }
        ticket.setEstado("CANCELADO");
        return toResponse(ticketRepository.save(ticket));
    }

    private TicketResponseDTO toResponse(Ticket t) {
        return TicketResponseDTO.builder()
                .id(t.getId())
                .usuarioId(t.getUsuarioId())
                .eventoId(t.getEventoId())
                .cantidad(t.getCantidad())
                .precioUnitario(t.getPrecioUnitario())
                .precioTotal(t.getPrecioTotal())
                .estado(t.getEstado())
                .creadoEn(t.getCreadoEn())
                .build();
    }
}