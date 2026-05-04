package com.tuapp.mseventos.service;

import com.tuapp.mseventos.dto.*;
import com.tuapp.mseventos.model.Evento;
import com.tuapp.mseventos.repository.EventoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
public class EventoService {

    private static final Logger log = LoggerFactory.getLogger(EventoService.class);
    private final EventoRepository eventoRepository;
    private final WebClient webClient;

    public EventoService(EventoRepository eventoRepository, WebClient.Builder webClientBuilder) {
        this.eventoRepository = eventoRepository;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public List<EventoResponseDTO> listarActivos() {
        log.info("Listando eventos activos");
        return eventoRepository.findByEstadoAndCapacidadDisponibleGreaterThan("ACTIVO", 0)
                .stream().map(this::toResponse).toList();
    }

    public List<EventoResponseDTO> listarTodos() {
        log.info("Listando todos los eventos");
        return eventoRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    public EventoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando evento con id: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado con id: " + id));
        return toResponse(evento);
    }

    public EventoResponseDTO crear(EventoRequestDTO dto) {
        log.info("Creando evento: {}", dto.getNombre());

        // Verificar que el organizador existe en ms-usuarios
        Boolean usuarioExiste = webClient.get()
                .uri("/api/usuarios/{id}/existe", dto.getOrganizadorId())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (usuarioExiste == null || !usuarioExiste) {
            log.warn("Organizador con id {} no existe o está inactivo", dto.getOrganizadorId());
            throw new RuntimeException("El organizador no existe o está inactivo");
        }

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        Evento evento = Evento.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .lugar(dto.getLugar())
                .capacidadTotal(dto.getCapacidadTotal())
                .precio(dto.getPrecio())
                .categoria(dto.getCategoria())
                .organizadorId(dto.getOrganizadorId())
                .build();

        Evento guardado = eventoRepository.save(evento);
        log.info("Evento creado con id: {}", guardado.getId());
        return toResponse(guardado);
    }

    public EventoResponseDTO actualizar(Long id, EventoRequestDTO dto) {
        log.info("Actualizando evento con id: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado con id: " + id));
        evento.setNombre(dto.getNombre());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setLugar(dto.getLugar());
        evento.setPrecio(dto.getPrecio());
        evento.setCategoria(dto.getCategoria());
        return toResponse(eventoRepository.save(evento));
    }

    public EventoResponseDTO cancelar(Long id) {
        log.info("Cancelando evento con id: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado con id: " + id));
        evento.setEstado("CANCELADO");
        return toResponse(eventoRepository.save(evento));
    }

    public boolean verificarDisponibilidad(Long id, Integer cantidad) {
        log.info("Verificando disponibilidad del evento: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado con id: " + id));
        return evento.getCapacidadDisponible() >= cantidad && evento.getEstado().equals("ACTIVO");
    }

    public void reducirCapacidad(Long id, Integer cantidad) {
        log.info("Reduciendo capacidad del evento: {} en {}", id, cantidad);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado con id: " + id));
        if (evento.getCapacidadDisponible() < cantidad) {
            throw new RuntimeException("No hay suficiente capacidad disponible");
        }
        evento.setCapacidadDisponible(evento.getCapacidadDisponible() - cantidad);
        if (evento.getCapacidadDisponible() == 0) {
            evento.setEstado("AGOTADO");
        }
        eventoRepository.save(evento);
    }

    private EventoResponseDTO toResponse(Evento e) {
        return EventoResponseDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .lugar(e.getLugar())
                .capacidadTotal(e.getCapacidadTotal())
                .capacidadDisponible(e.getCapacidadDisponible())
                .precio(e.getPrecio())
                .categoria(e.getCategoria())
                .estado(e.getEstado())
                .organizadorId(e.getOrganizadorId())
                .creadoEn(e.getCreadoEn())
                .build();
    }
}