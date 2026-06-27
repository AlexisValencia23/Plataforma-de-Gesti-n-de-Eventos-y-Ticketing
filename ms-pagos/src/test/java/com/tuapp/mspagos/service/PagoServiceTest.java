package com.tuapp.mspagos.service;

import com.tuapp.mspagos.dto.PagoRequestDTO;
import com.tuapp.mspagos.dto.PagoResponseDTO;
import com.tuapp.mspagos.model.Pago;
import com.tuapp.mspagos.repository.PagoRepository;
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
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

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

    private PagoService pagoService;

    private Pago pagoCompletado;
    private PagoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        pagoService = new PagoService(pagoRepository, webClientBuilder);

        // Pago base reutilizable
        pagoCompletado = Pago.builder()
                .id(1L)
                .ticketId(1L)
                .usuarioId(1L)
                .monto(100000.0)
                .metodoPago("TARJETA")
                .estado("COMPLETADO")
                .referencia("REF-123456")
                .creadoEn(LocalDateTime.now())
                .build();

        // DTO base reutilizable
        requestDTO = new PagoRequestDTO();
        requestDTO.setTicketId(1L);
        requestDTO.setUsuarioId(1L);
        requestDTO.setMonto(100000.0);
        requestDTO.setMetodoPago("TARJETA");
    }

    // =============================================
    // TESTS: listarTodos()
    // =============================================

    @Test
    void listarTodos_debeRetornarTodosLosPagos() {
        // GIVEN
        when(pagoRepository.findAll()).thenReturn(List.of(pagoCompletado));

        // WHEN
        List<PagoResponseDTO> resultado = pagoService.listarTodos();

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void listarTodos_cuandoNoHayPagos_debeRetornarListaVacia() {
        // GIVEN
        when(pagoRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<PagoResponseDTO> resultado = pagoService.listarTodos();

        // THEN
        assertTrue(resultado.isEmpty());
    }

    // =============================================
    // TESTS: listarPorUsuario()
    // =============================================

    @Test
    void listarPorUsuario_debeRetornarPagosDelUsuario() {
        // GIVEN
        when(pagoRepository.findByUsuarioId(1L)).thenReturn(List.of(pagoCompletado));

        // WHEN
        List<PagoResponseDTO> resultado = pagoService.listarPorUsuario(1L);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getUsuarioId());
        verify(pagoRepository).findByUsuarioId(1L);
    }

    // =============================================
    // TESTS: obtenerPorId()
    // =============================================

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarPago() {
        // GIVEN
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoCompletado));

        // WHEN
        PagoResponseDTO resultado = pagoService.obtenerPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("COMPLETADO", resultado.getEstado());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> pagoService.obtenerPorId(99L));
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
                () -> pagoService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("usuario"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoTicketNoEstaPendiente_debeLanzarExcepcion() {
        // GIVEN
        Map<String, Object> ticketData = Map.of(
                "estado", "CONFIRMADO",
                "precioTotal", 100000.0
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(ticketData));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("PENDIENTE"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoMontoIncorrecto_debeLanzarExcepcion() {
        // GIVEN: ticket pendiente con precio diferente al monto enviado
        Map<String, Object> ticketData = Map.of(
                "estado", "PENDIENTE",
                "precioTotal", 200000.0  // distinto al monto del DTO (100000)
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(ticketData));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("monto"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoMetodoPagoInvalido_debeLanzarExcepcion() {
        // GIVEN
        requestDTO.setMetodoPago("BITCOIN"); // método inválido
        Map<String, Object> ticketData = Map.of(
                "estado", "PENDIENTE",
                "precioTotal", 100000.0
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(ticketData));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("Metodo de pago invalido"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoDatosValidos_debeCrearPagoYConfirmarTicket() {
        // GIVEN
        Map<String, Object> ticketData = Map.of(
                "estado", "PENDIENTE",
                "precioTotal", 100000.0
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(ticketData));

        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoCompletado);

        // WHEN
        PagoResponseDTO resultado = pagoService.crear(requestDTO);

        // THEN
        assertNotNull(resultado);
        assertEquals("COMPLETADO", resultado.getEstado());
        assertEquals("TARJETA", resultado.getMetodoPago());
        verify(pagoRepository, times(2)).save(any(Pago.class));
    }

    // =============================================
    // TESTS: reembolsar()
    // =============================================

    @Test
    @SuppressWarnings("unchecked")
    void reembolsar_cuandoPagoCompletado_debeCambiarAReembolsado() {
        // GIVEN
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoCompletado));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of()));

        // WHEN
        PagoResponseDTO resultado = pagoService.reembolsar(1L);

        // THEN
        assertEquals("REEMBOLSADO", resultado.getEstado());
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void reembolsar_cuandoPagoNoEstaCompletado_debeLanzarExcepcion() {
        // GIVEN
        pagoCompletado.setEstado("PENDIENTE");
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoCompletado));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.reembolsar(1L));
        assertTrue(ex.getMessage().contains("completados"));
    }

    @Test
    void reembolsar_cuandoPagoNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () -> pagoService.reembolsar(99L));
    }

    // =============================================
    // TESTS: validaciones de método de pago
    // =============================================

    @Test
    @SuppressWarnings("unchecked")
    void crear_conMetodoPagoTransferencia_debeSerValido() {
        // GIVEN
        requestDTO.setMetodoPago("TRANSFERENCIA");
        Map<String, Object> ticketData = Map.of(
                "estado", "PENDIENTE",
                "precioTotal", 100000.0
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(ticketData));

        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoCompletado);

        // WHEN
        PagoResponseDTO resultado = pagoService.crear(requestDTO);

        // THEN
        assertNotNull(resultado);
        verify(pagoRepository, times(2)).save(any(Pago.class));
    }
}