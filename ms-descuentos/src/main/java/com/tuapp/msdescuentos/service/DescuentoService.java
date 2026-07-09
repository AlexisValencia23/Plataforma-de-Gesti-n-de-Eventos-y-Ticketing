package com.tuapp.msdescuentos.service;

import com.tuapp.msdescuentos.dto.*;
import com.tuapp.msdescuentos.model.Descuento;
import com.tuapp.msdescuentos.repository.DescuentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DescuentoService {

    private static final Logger log = LoggerFactory.getLogger(DescuentoService.class);
    private final DescuentoRepository descuentoRepository;

    public DescuentoService(DescuentoRepository descuentoRepository) {
        this.descuentoRepository = descuentoRepository;
    }

    public List<DescuentoResponseDTO> listarActivos() {
        log.info("Listando descuentos activos");
        return descuentoRepository.findByActivoTrue()
                .stream().map(this::toResponse).toList();
    }

    public List<DescuentoResponseDTO> listarTodos() {
        log.info("Listando todos los descuentos");
        return descuentoRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    public DescuentoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando descuento con id: {}", id);
        Descuento descuento = descuentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Descuento no encontrado con id: " + id));
        return toResponse(descuento);
    }

    public DescuentoResponseDTO crear(DescuentoRequestDTO dto) {
        log.info("Creando descuento con codigo: {}", dto.getCodigo());

        if (descuentoRepository.existsByCodigo(dto.getCodigo().toUpperCase())) {
            log.warn("Codigo de descuento ya existe: {}", dto.getCodigo());
            throw new RuntimeException("Ya existe un descuento con ese codigo");
        }

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        Descuento descuento = Descuento.builder()
                .codigo(dto.getCodigo().toUpperCase())
                .descripcion(dto.getDescripcion())
                .porcentaje(dto.getPorcentaje())
                .usoMaximo(dto.getUsoMaximo())
                .usoActual(0)
                .activo(true)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .build();

        Descuento guardado = descuentoRepository.save(descuento);
        log.info("Descuento creado con id: {}", guardado.getId());
        return toResponse(guardado);
    }

    public DescuentoResponseDTO validar(String codigo) {
        log.info("Validando codigo de descuento: {}", codigo);
        Descuento descuento = descuentoRepository.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new NoSuchElementException("Codigo de descuento no encontrado: " + codigo));

        if (!descuento.getActivo()) {
            throw new RuntimeException("El descuento no esta activo");
        }

        if (descuento.getUsoActual() >= descuento.getUsoMaximo()) {
            throw new RuntimeException("El descuento ha alcanzado su limite de uso");
        }

        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isBefore(descuento.getFechaInicio())) {
            throw new RuntimeException("El descuento aun no esta vigente");
        }

        if (ahora.isAfter(descuento.getFechaFin())) {
            descuento.setActivo(false);
            descuentoRepository.save(descuento);
            throw new RuntimeException("El descuento ha expirado");
        }

        // Incrementar uso
        descuento.setUsoActual(descuento.getUsoActual() + 1);
        if (descuento.getUsoActual() >= descuento.getUsoMaximo()) {
            descuento.setActivo(false);
        }
        descuentoRepository.save(descuento);

        log.info("Descuento validado correctamente: {}% de descuento", descuento.getPorcentaje());
        return toResponse(descuento);
    }

    public void desactivar(Long id) {
        log.info("Desactivando descuento con id: {}", id);
        Descuento descuento = descuentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Descuento no encontrado con id: " + id));
        descuento.setActivo(false);
        descuentoRepository.save(descuento);
        log.info("Descuento desactivado correctamente");
    }

    private DescuentoResponseDTO toResponse(Descuento d) {
        return DescuentoResponseDTO.builder()
                .id(d.getId())
                .codigo(d.getCodigo())
                .descripcion(d.getDescripcion())
                .porcentaje(d.getPorcentaje())
                .usoMaximo(d.getUsoMaximo())
                .usoActual(d.getUsoActual())
                .activo(d.getActivo())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .creadoEn(d.getCreadoEn())
                .build();
    }
}