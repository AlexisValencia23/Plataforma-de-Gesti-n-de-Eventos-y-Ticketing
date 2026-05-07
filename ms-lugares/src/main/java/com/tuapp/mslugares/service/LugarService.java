package com.tuapp.mslugares.service;

import com.tuapp.mslugares.dto.*;
import com.tuapp.mslugares.model.Lugar;
import com.tuapp.mslugares.repository.LugarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LugarService {

    private static final Logger log = LoggerFactory.getLogger(LugarService.class);
    private final LugarRepository lugarRepository;

    public LugarService(LugarRepository lugarRepository) {
        this.lugarRepository = lugarRepository;
    }

    public List<LugarResponseDTO> listarActivos() {
        log.info("Listando lugares activos");
        return lugarRepository.findByActivoTrue()
                .stream().map(this::toResponse).toList();
    }

    public List<LugarResponseDTO> listarPorCiudad(String ciudad) {
        log.info("Listando lugares en ciudad: {}", ciudad);
        return lugarRepository.findByCiudad(ciudad)
                .stream().map(this::toResponse).toList();
    }

    public List<LugarResponseDTO> listarPorCapacidad(Integer capacidad) {
        log.info("Listando lugares con capacidad minima: {}", capacidad);
        return lugarRepository.findByCapacidadMaximaGreaterThanEqual(capacidad)
                .stream().map(this::toResponse).toList();
    }

    public LugarResponseDTO obtenerPorId(Long id) {
        log.info("Buscando lugar con id: {}", id);
        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lugar no encontrado con id: " + id));
        return toResponse(lugar);
    }

    public LugarResponseDTO crear(LugarRequestDTO dto) {
        log.info("Creando lugar: {}", dto.getNombre());
        Lugar lugar = Lugar.builder()
                .nombre(dto.getNombre())
                .direccion(dto.getDireccion())
                .ciudad(dto.getCiudad())
                .pais(dto.getPais())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .descripcion(dto.getDescripcion())
                .telefono(dto.getTelefono())
                .activo(true)
                .build();
        Lugar guardado = lugarRepository.save(lugar);
        log.info("Lugar creado con id: {}", guardado.getId());
        return toResponse(guardado);
    }

    public LugarResponseDTO actualizar(Long id, LugarRequestDTO dto) {
        log.info("Actualizando lugar con id: {}", id);
        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lugar no encontrado con id: " + id));
        lugar.setNombre(dto.getNombre());
        lugar.setDireccion(dto.getDireccion());
        lugar.setCiudad(dto.getCiudad());
        lugar.setPais(dto.getPais());
        lugar.setCapacidadMaxima(dto.getCapacidadMaxima());
        lugar.setDescripcion(dto.getDescripcion());
        lugar.setTelefono(dto.getTelefono());
        return toResponse(lugarRepository.save(lugar));
    }

    public void eliminar(Long id) {
        log.info("Desactivando lugar con id: {}", id);
        Lugar lugar = lugarRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lugar no encontrado con id: " + id));
        lugar.setActivo(false);
        lugarRepository.save(lugar);
        log.info("Lugar desactivado correctamente");
    }

    private LugarResponseDTO toResponse(Lugar l) {
        return LugarResponseDTO.builder()
                .id(l.getId())
                .nombre(l.getNombre())
                .direccion(l.getDireccion())
                .ciudad(l.getCiudad())
                .pais(l.getPais())
                .capacidadMaxima(l.getCapacidadMaxima())
                .descripcion(l.getDescripcion())
                .telefono(l.getTelefono())
                .activo(l.getActivo())
                .build();
    }
}