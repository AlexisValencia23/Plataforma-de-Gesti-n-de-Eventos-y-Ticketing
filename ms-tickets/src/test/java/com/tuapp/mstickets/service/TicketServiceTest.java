package com.tuapp.mstickets.service;

import com.tuapp.mstickets.dto.TicketRequestDTO;
import com.tuapp.mstickets.dto.TicketResponseDTO;
import com.tuapp.mstickets.model.Ticket;
import com.tuapp.mstickets.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestBodySpec requestBodySpec;

    private TicketService ticketService;

    private Ticket ticketPendiente;
    private TicketRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        // El Builder siempre retorna el mismo webClient mock
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        // Crear el servicio manualmente
        ticketService = new TicketService(ticketRepository, webClientBuilder);

        // Ticket base reutilizable
        ticketPendiente = Ticket.builder()
                .id(1L)
                .usuarioId(1L)
                .eventoId(1L)
                .cantidad(2)
                .precioUnitario(50000.0)
                .precioTotal(100000.0)
                .estado("PENDIENTE")
                .creadoEn(LocalDateTime.now())
                .build();

        // DTO base reutilizable
        requestDTO = new TicketRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setEventoId(1L);
        requestDTO.setCantidad(2);
    }

    // =============================================
    // TESTS: listarTodos()
    // =============================================

    @Test
    void listarTodos_debeRetornarTodosLosTickets() {
        // GIVEN
        when(ticketRepository.findAll()).thenReturn(List.of(ticketPendiente));

        // WHEN
        List<TicketResponseDTO> resultado = ticketService.listarTodos();

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void listarTodos_cuandoNoHayTickets_debeRetornarListaVacia() {
        // GIVEN
        when(ticketRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<TicketResponseDTO> resultado = ticketService.listarTodos();

        // THEN
        assertTrue(resultado.isEmpty());
    }

    // =============================================
    // TESTS: listarPorUsuario()
    // =============================================

    @Test
    void listarPorUsuario_debeRetornarTicketsDelUsuario() {
        // GIVEN
        when(ticketRepository.findByUsuarioId(1L)).thenReturn(List.of(ticketPendiente));

        // WHEN
        List<TicketResponseDTO> resultado = ticketService.listarPorUsuario(1L);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getUsuarioId());
        verify(ticketRepository).findByUsuarioId(1L);
    }

    // =============================================
    // TESTS: listarPorEvento()
    // =============================================

    @Test
    void listarPorEvento_debeRetornarTicketsDelEvento() {
        // GIVEN
        when(ticketRepository.findByEventoId(1L)).thenReturn(List.of(ticketPendiente));

        // WHEN
        List<TicketResponseDTO> resultado = ticketService.listarPorEvento(1L);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getEventoId());
        verify(ticketRepository).findByEventoId(1L);
    }

    // =============================================
    // TESTS: obtenerPorId()
    // =============================================

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarTicket() {
        // GIVEN
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketPendiente));

        // WHEN
        TicketResponseDTO resultado = ticketService.obtenerPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> ticketService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // =============================================
    // TESTS: crear()
    // =============================================

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ticketService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("usuario"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoEventoSinDisponibilidad_debeLanzarExcepcion() {
        // GIVEN: usuario existe, evento sin disponibilidad
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        // Primera llamada: usuario existe, segunda: sin disponibilidad
        when(responseSpec.bodyToMono(Boolean.class))
                .thenReturn(Mono.just(true))
                .thenReturn(Mono.just(false));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ticketService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("disponibilidad"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoDatosValidos_debeCrearTicketCorrectamente() {
        // GIVEN
        Map<String, Object> eventoData = Map.of("precio", 50000.0);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class))
                .thenReturn(Mono.just(true))   // usuario existe
                .thenReturn(Mono.just(true));  // evento disponible
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(eventoData));

        // Mock para el PATCH de reducir capacidad
        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketPendiente);

        // WHEN
        TicketResponseDTO resultado = ticketService.crear(requestDTO);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getUsuarioId());
        assertEquals(1L, resultado.getEventoId());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    // =============================================
    // TESTS: confirmar()
    // =============================================

    @Test
    void confirmar_cuandoTicketEstaPendiente_debeCambiarAConfirmado() {
        // GIVEN
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketPendiente));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        TicketResponseDTO resultado = ticketService.confirmar(1L);

        // THEN
        assertEquals("CONFIRMADO", resultado.getEstado());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void confirmar_cuandoTicketNoEstaPendiente_debeLanzarExcepcion() {
        // GIVEN
        ticketPendiente.setEstado("CONFIRMADO");
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketPendiente));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ticketService.confirmar(1L));
        assertTrue(ex.getMessage().contains("PENDIENTE"));
    }

    @Test
    void confirmar_cuandoTicketNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () -> ticketService.confirmar(99L));
    }

    // =============================================
    // TESTS: cancelar()
    // =============================================

    @Test
    void cancelar_cuandoTicketNoCancelado_debeCambiarACancelado() {
        // GIVEN
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketPendiente));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        TicketResponseDTO resultado = ticketService.cancelar(1L);

        // THEN
        assertEquals("CANCELADO", resultado.getEstado());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void cancelar_cuandoTicketYaCancelado_debeLanzarExcepcion() {
        // GIVEN
        ticketPendiente.setEstado("CANCELADO");
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketPendiente));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ticketService.cancelar(1L));
        assertTrue(ex.getMessage().contains("cancelado"));
    }

    @Test
    void cancelar_cuandoTicketNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () -> ticketService.cancelar(99L));
    }

    // =============================================
    // TESTS: precioTotal calculado correctamente
    // =============================================

    @Test
    void obtenerPorId_debeRetornarPrecioTotalCorrecto() {
        // GIVEN
        ticketPendiente.setPrecioUnitario(50000.0);
        ticketPendiente.setPrecioTotal(100000.0); // 2 tickets * 50000
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketPendiente));

        // WHEN
        TicketResponseDTO resultado = ticketService.obtenerPorId(1L);

        // THEN
        assertEquals(100000.0, resultado.getPrecioTotal());
        assertEquals(50000.0, resultado.getPrecioUnitario());
    }
}