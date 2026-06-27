package com.tuapp.msreservas.service;

import com.tuapp.msreservas.dto.ReservaRequestDTO;
import com.tuapp.msreservas.dto.ReservaResponseDTO;
import com.tuapp.msreservas.model.Reserva;
import com.tuapp.msreservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

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

    private ReservaService reservaService;

    private Reserva reservaPendiente;
    private ReservaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        reservaService = new ReservaService(reservaRepository, webClientBuilder);

        // Reserva base reutilizable (no expirada)
        reservaPendiente = Reserva.builder()
                .id(1L)
                .usuarioId(1L)
                .eventoId(1L)
                .cantidad(2)
                .estado("PENDIENTE")
                .creadoEn(LocalDateTime.now())
                .expiraEn(LocalDateTime.now().plusMinutes(15)) // aún vigente
                .build();

        // DTO base reutilizable
        requestDTO = new ReservaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setEventoId(1L);
        requestDTO.setCantidad(2);
    }

    // =============================================
    // TESTS: listarTodas()
    // =============================================

    @Test
    void listarTodas_debeRetornarTodasLasReservas() {
        // GIVEN
        when(reservaRepository.findAll()).thenReturn(List.of(reservaPendiente));

        // WHEN
        List<ReservaResponseDTO> resultado = reservaService.listarTodas();

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void listarTodas_cuandoNoHayReservas_debeRetornarListaVacia() {
        // GIVEN
        when(reservaRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<ReservaResponseDTO> resultado = reservaService.listarTodas();

        // THEN
        assertTrue(resultado.isEmpty());
    }

    // =============================================
    // TESTS: listarPorUsuario()
    // =============================================

    @Test
    void listarPorUsuario_debeRetornarReservasDelUsuario() {
        // GIVEN
        when(reservaRepository.findByUsuarioId(1L)).thenReturn(List.of(reservaPendiente));

        // WHEN
        List<ReservaResponseDTO> resultado = reservaService.listarPorUsuario(1L);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getUsuarioId());
        verify(reservaRepository).findByUsuarioId(1L);
    }

    // =============================================
    // TESTS: listarPorEvento()
    // =============================================

    @Test
    void listarPorEvento_debeRetornarReservasDelEvento() {
        // GIVEN
        when(reservaRepository.findByEventoId(1L)).thenReturn(List.of(reservaPendiente));

        // WHEN
        List<ReservaResponseDTO> resultado = reservaService.listarPorEvento(1L);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getEventoId());
        verify(reservaRepository).findByEventoId(1L);
    }

    // =============================================
    // TESTS: obtenerPorId()
    // =============================================

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarReserva() {
        // GIVEN
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));

        // WHEN
        ReservaResponseDTO resultado = reservaService.obtenerPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> reservaService.obtenerPorId(99L));
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
                () -> reservaService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("usuario"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoEventoSinDisponibilidad_debeLanzarExcepcion() {
        // GIVEN
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class))
                .thenReturn(Mono.just(true))   // usuario existe
                .thenReturn(Mono.just(false)); // sin disponibilidad

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservaService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("disponibilidad"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoDatosValidos_debeCrearReservaConExpiracion() {
        // GIVEN
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class))
                .thenReturn(Mono.just(true))  // usuario existe
                .thenReturn(Mono.just(true)); // evento disponible

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaPendiente);

        // WHEN
        ReservaResponseDTO resultado = reservaService.crear(requestDTO);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getUsuarioId());
        assertEquals(1L, resultado.getEventoId());
        assertNotNull(resultado.getExpiraEn());
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    // =============================================
    // TESTS: confirmar()
    // =============================================

    @Test
    void confirmar_cuandoReservaVigente_debeCambiarAConfirmada() {
        // GIVEN: reserva pendiente y no expirada
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        ReservaResponseDTO resultado = reservaService.confirmar(1L);

        // THEN
        assertEquals("CONFIRMADA", resultado.getEstado());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void confirmar_cuandoReservaExpirada_debeLanzarExcepcionYCancelar() {
        // GIVEN: reserva con fecha de expiración en el pasado
        reservaPendiente.setExpiraEn(LocalDateTime.now().minusMinutes(5));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservaService.confirmar(1L));
        assertTrue(ex.getMessage().contains("expirado"));
        assertEquals("CANCELADA", reservaPendiente.getEstado());
    }

    @Test
    void confirmar_cuandoReservaNoEstaPendiente_debeLanzarExcepcion() {
        // GIVEN
        reservaPendiente.setEstado("CONFIRMADA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservaService.confirmar(1L));
        assertTrue(ex.getMessage().contains("PENDIENTE"));
    }

    @Test
    void confirmar_cuandoReservaNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () -> reservaService.confirmar(99L));
    }

    // =============================================
    // TESTS: cancelar()
    // =============================================

    @Test
    void cancelar_cuandoReservaNoCancelada_debeCambiarACancelada() {
        // GIVEN
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        ReservaResponseDTO resultado = reservaService.cancelar(1L);

        // THEN
        assertEquals("CANCELADA", resultado.getEstado());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void cancelar_cuandoReservaYaCancelada_debeLanzarExcepcion() {
        // GIVEN
        reservaPendiente.setEstado("CANCELADA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservaService.cancelar(1L));
        assertTrue(ex.getMessage().contains("cancelada"));
    }

    @Test
    void cancelar_cuandoReservaNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () -> reservaService.cancelar(99L));
    }
}