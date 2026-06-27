package com.tuapp.mseventos.service;

import com.tuapp.mseventos.dto.EventoRequestDTO;
import com.tuapp.mseventos.dto.EventoResponseDTO;
import com.tuapp.mseventos.model.Evento;
import com.tuapp.mseventos.repository.EventoRepository;
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
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    // Se crea manualmente para evitar el problema con WebClient.Builder
    private EventoService eventoService;

    private Evento eventoActivo;
    private EventoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        // Configurar el builder para que retorne el webClient mock
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        // Crear el servicio manualmente
        eventoService = new EventoService(eventoRepository, webClientBuilder);

        // Evento base reutilizable
        eventoActivo = Evento.builder()
                .id(1L)
                .nombre("Concierto de Rock")
                .descripcion("Gran concierto en vivo")
                .fechaInicio(LocalDateTime.now().plusDays(5))
                .fechaFin(LocalDateTime.now().plusDays(5).plusHours(4))
                .lugar("Estadio Nacional")
                .capacidadTotal(1000)
                .capacidadDisponible(1000)
                .precio(50000.0)
                .categoria("MUSICA")
                .estado("ACTIVO")
                .organizadorId(1L)
                .creadoEn(LocalDateTime.now())
                .build();

        // DTO base reutilizable
        requestDTO = new EventoRequestDTO();
        requestDTO.setNombre("Concierto de Rock");
        requestDTO.setDescripcion("Gran concierto en vivo");
        requestDTO.setFechaInicio(LocalDateTime.now().plusDays(5));
        requestDTO.setFechaFin(LocalDateTime.now().plusDays(5).plusHours(4));
        requestDTO.setLugar("Estadio Nacional");
        requestDTO.setCapacidadTotal(1000);
        requestDTO.setPrecio(50000.0);
        requestDTO.setCategoria("MUSICA");
        requestDTO.setOrganizadorId(1L);
    }

    // =============================================
    // TESTS: listarActivos()
    // =============================================

    @Test
    void listarActivos_cuandoHayEventos_debeRetornarLista() {
        // GIVEN
        when(eventoRepository.findByEstadoAndCapacidadDisponibleGreaterThan("ACTIVO", 0))
                .thenReturn(List.of(eventoActivo));

        // WHEN
        List<EventoResponseDTO> resultado = eventoService.listarActivos();

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Concierto de Rock", resultado.get(0).getNombre());
        assertEquals("ACTIVO", resultado.get(0).getEstado());
        verify(eventoRepository, times(1))
                .findByEstadoAndCapacidadDisponibleGreaterThan("ACTIVO", 0);
    }

    @Test
    void listarActivos_cuandoNoHayEventos_debeRetornarListaVacia() {
        // GIVEN
        when(eventoRepository.findByEstadoAndCapacidadDisponibleGreaterThan("ACTIVO", 0))
                .thenReturn(List.of());

        // WHEN
        List<EventoResponseDTO> resultado = eventoService.listarActivos();

        // THEN
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =============================================
    // TESTS: listarTodos()
    // =============================================

    @Test
    void listarTodos_debeRetornarTodosLosEventos() {
        // GIVEN
        Evento eventoCancelado = Evento.builder()
                .id(2L).nombre("Evento Cancelado").descripcion("desc")
                .fechaInicio(LocalDateTime.now().plusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(1).plusHours(2))
                .lugar("Lugar").capacidadTotal(100).capacidadDisponible(0)
                .precio(1000.0).categoria("OTRO").estado("CANCELADO")
                .organizadorId(1L).creadoEn(LocalDateTime.now())
                .build();

        when(eventoRepository.findAll()).thenReturn(List.of(eventoActivo, eventoCancelado));

        // WHEN
        List<EventoResponseDTO> resultado = eventoService.listarTodos();

        // THEN
        assertEquals(2, resultado.size());
        verify(eventoRepository, times(1)).findAll();
    }

    // =============================================
    // TESTS: obtenerPorId()
    // =============================================

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarEvento() {
        // GIVEN
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoActivo));

        // WHEN
        EventoResponseDTO resultado = eventoService.obtenerPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Concierto de Rock", resultado.getNombre());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> eventoService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // =============================================
    // TESTS: crear()
    // =============================================

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoFechaFinEsAnteriorAFechaInicio_debeLanzarExcepcion() {
        // GIVEN
        requestDTO.setFechaInicio(LocalDateTime.now().plusDays(5));
        requestDTO.setFechaFin(LocalDateTime.now().plusDays(1));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("fecha de fin"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoOrganizadorNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.crear(requestDTO));
        assertTrue(ex.getMessage().contains("organizador"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_cuandoDatosValidos_debeGuardarYRetornarEvento() {
        // GIVEN
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoActivo);

        // WHEN
        EventoResponseDTO resultado = eventoService.crear(requestDTO);

        // THEN
        assertNotNull(resultado);
        assertEquals("Concierto de Rock", resultado.getNombre());
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    // =============================================
    // TESTS: cancelar()
    // =============================================

    @Test
    void cancelar_cuandoEventoExiste_debeCambiarEstadoACancelado() {
        // GIVEN
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoActivo));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        EventoResponseDTO resultado = eventoService.cancelar(1L);

        // THEN
        assertEquals("CANCELADO", resultado.getEstado());
        verify(eventoRepository).save(any(Evento.class));
    }

    @Test
    void cancelar_cuandoEventoNoExiste_debeLanzarExcepcion() {
        // GIVEN
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () -> eventoService.cancelar(99L));
    }

    // =============================================
    // TESTS: verificarDisponibilidad()
    // =============================================

    @Test
    void verificarDisponibilidad_cuandoHayCapacidadYEstaActivo_debeRetornarTrue() {
        // GIVEN
        eventoActivo.setCapacidadDisponible(100);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoActivo));

        // WHEN
        boolean resultado = eventoService.verificarDisponibilidad(1L, 50);

        // THEN
        assertTrue(resultado);
    }

    @Test
    void verificarDisponibilidad_cuandoNoHayCapacidadSuficiente_debeRetornarFalse() {
        // GIVEN
        eventoActivo.setCapacidadDisponible(10);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoActivo));

        // WHEN
        boolean resultado = eventoService.verificarDisponibilidad(1L, 50);

        // THEN
        assertFalse(resultado);
    }

    @Test
    void verificarDisponibilidad_cuandoEventoCancelado_debeRetornarFalse() {
        // GIVEN
        eventoActivo.setEstado("CANCELADO");
        eventoActivo.setCapacidadDisponible(500);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoActivo));

        // WHEN
        boolean resultado = eventoService.verificarDisponibilidad(1L, 10);

        // THEN
        assertFalse(resultado);
    }

    // =============================================
    // TESTS: reducirCapacidad()
    // =============================================

    @Test
    void reducirCapacidad_cuandoHayCapacidad_debeReducirCorrectamente() {
        // GIVEN
        eventoActivo.setCapacidadDisponible(100);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoActivo));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        eventoService.reducirCapacidad(1L, 30);

        // THEN
        assertEquals(70, eventoActivo.getCapacidadDisponible());
        verify(eventoRepository).save(eventoActivo);
    }

    @Test
    void reducirCapacidad_cuandoCapacidadLlegaACero_debeCambiarEstadoAAgotado() {
        // GIVEN
        eventoActivo.setCapacidadDisponible(10);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoActivo));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        eventoService.reducirCapacidad(1L, 10);

        // THEN
        assertEquals(0, eventoActivo.getCapacidadDisponible());
        assertEquals("AGOTADO", eventoActivo.getEstado());
    }

    @Test
    void reducirCapacidad_cuandoNoHaySuficienteCapacidad_debeLanzarExcepcion() {
        // GIVEN
        eventoActivo.setCapacidadDisponible(5);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoActivo));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.reducirCapacidad(1L, 100));
        assertTrue(ex.getMessage().contains("capacidad"));
    }
}