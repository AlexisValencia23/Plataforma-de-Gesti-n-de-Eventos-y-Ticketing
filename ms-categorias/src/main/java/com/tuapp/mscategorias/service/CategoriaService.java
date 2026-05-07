package com.tuapp.mscategorias.service;

import com.tuapp.mscategorias.dto.*;
import com.tuapp.mscategorias.model.Categoria;
import com.tuapp.mscategorias.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponseDTO> listarActivas() {
        log.info("Listando categorias activas");
        return categoriaRepository.findByActivoTrue()
                .stream().map(this::toResponse).toList();
    }

    public List<CategoriaResponseDTO> listarTodas() {
        log.info("Listando todas las categorias");
        return categoriaRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    public CategoriaResponseDTO obtenerPorId(Long id) {
        log.info("Buscando categoria con id: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria no encontrada con id: " + id));
        return toResponse(categoria);
    }

    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        log.info("Creando categoria: {}", dto.getNombre());
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            log.warn("Categoria ya existe: {}", dto.getNombre());
            throw new RuntimeException("Ya existe una categoria con ese nombre");
        }
        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .activo(true)
                .build();
        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoria creada con id: {}", guardada.getId());
        return toResponse(guardada);
    }

    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        log.info("Actualizando categoria con id: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria no encontrada con id: " + id));
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return toResponse(categoriaRepository.save(categoria));
    }

    public void eliminar(Long id) {
        log.info("Desactivando categoria con id: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria no encontrada con id: " + id));
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
        log.info("Categoria desactivada correctamente");
    }

    private CategoriaResponseDTO toResponse(Categoria c) {
        return CategoriaResponseDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .activo(c.getActivo())
                .build();
    }
}