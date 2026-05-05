package com.tuapp.mspagos.service;

import com.tuapp.mspagos.dto.*;
import com.tuapp.mspagos.model.Pago;
import com.tuapp.mspagos.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);
    private final PagoRepository pagoRepository;
    private final WebClient webClientTickets;
    private final WebClient webClientUsuarios;

    public PagoService(PagoRepository pagoRepository, WebClient.Builder webClientBuilder) {
        this.pagoRepository = pagoRepository;
        this.webClientTickets = webClientBuilder.baseUrl("http://localhost:8083").build();
        this.webClientUsuarios = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public List<PagoResponseDTO> listarTodos() {
        log.info("Listando todos los pagos");
        return pagoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<PagoResponseDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando pagos del usuario: {}", usuarioId);
        return pagoRepository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    public PagoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando pago con id: {}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado con id: " + id));
        return toResponse(pago);
    }

    public PagoResponseDTO crear(PagoRequestDTO dto) {
        log.info("Procesando pago para ticket: {}", dto.getTicketId());

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

        // Verificar que el ticket existe y esta en estado PENDIENTE
        Map ticketData = webClientTickets.get()
                .uri("/api/tickets/{id}", dto.getTicketId())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (ticketData == null) {
            throw new RuntimeException("El ticket no existe");
        }

        String estadoTicket = (String) ticketData.get("estado");
        if (!estadoTicket.equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden pagar tickets en estado PENDIENTE");
        }

        // Verificar que el monto coincide con el precio total del ticket
        Double precioTotal = ((Number) ticketData.get("precioTotal")).doubleValue();
        if (!dto.getMonto().equals(precioTotal)) {
            log.warn("Monto incorrecto. Esperado: {} Recibido: {}", precioTotal, dto.getMonto());
            throw new RuntimeException("El monto no coincide con el precio total del ticket: " + precioTotal);
        }

        // Verificar metodo de pago valido
        List<String> metodos = List.of("TARJETA", "TRANSFERENCIA", "EFECTIVO");
        if (!metodos.contains(dto.getMetodoPago().toUpperCase())) {
            throw new RuntimeException("Metodo de pago invalido. Use: TARJETA, TRANSFERENCIA o EFECTIVO");
        }

        // Crear el pago
        Pago pago = Pago.builder()
                .ticketId(dto.getTicketId())
                .usuarioId(dto.getUsuarioId())
                .monto(dto.getMonto())
                .metodoPago(dto.getMetodoPago().toUpperCase())
                .build();

        Pago guardado = pagoRepository.save(pago);
        log.info("Pago creado con id: {}", guardado.getId());

        // Confirmar el ticket en ms-tickets
        webClientTickets.patch()
                .uri("/api/tickets/{id}/confirmar", dto.getTicketId())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Actualizar estado del pago a COMPLETADO
        guardado.setEstado("COMPLETADO");
        pagoRepository.save(guardado);
        log.info("Pago completado y ticket confirmado");

        return toResponse(guardado);
    }

    public PagoResponseDTO reembolsar(Long id) {
        log.info("Reembolsando pago con id: {}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado con id: " + id));

        if (!pago.getEstado().equals("COMPLETADO")) {
            throw new RuntimeException("Solo se pueden reembolsar pagos completados");
        }

        pago.setEstado("REEMBOLSADO");
        pagoRepository.save(pago);

        // Cancelar el ticket en ms-tickets
        webClientTickets.patch()
                .uri("/api/tickets/{id}/cancelar", pago.getTicketId())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("Pago reembolsado y ticket cancelado");
        return toResponse(pago);
    }

    private PagoResponseDTO toResponse(Pago p) {
        return PagoResponseDTO.builder()
                .id(p.getId())
                .ticketId(p.getTicketId())
                .usuarioId(p.getUsuarioId())
                .monto(p.getMonto())
                .metodoPago(p.getMetodoPago())
                .estado(p.getEstado())
                .referencia(p.getReferencia())
                .creadoEn(p.getCreadoEn())
                .build();
    }
}