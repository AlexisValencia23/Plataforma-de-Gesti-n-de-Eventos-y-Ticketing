package com.tuapp.msusuarios.service;

import com.tuapp.msusuarios.dto.*;
import com.tuapp.msusuarios.model.Usuario;
import com.tuapp.msusuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioResponseDTO> listarActivos() {
        log.info("Listando usuarios activos");
        return usuarioRepository.findByActivoTrue()
                .stream().map(this::toResponse).toList();
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        log.info("Buscando usuario con id: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + id));
        return toResponse(usuario);
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        log.info("Creando usuario con email: {}", dto.getEmail());
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email ya registrado: {}", dto.getEmail());
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .telefono(dto.getTelefono())
                .rol(dto.getRol())
                .build();
        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado con id: {}", guardado.getId());
        return toResponse(guardado);
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        log.info("Actualizando usuario con id: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + id));
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(dto.getRol());
        return toResponse(usuarioRepository.save(usuario));
    }

    public void eliminar(Long id) {
        log.info("Desactivando usuario con id: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado correctamente");
    }

    public boolean existeYActivo(Long id) {
        return usuarioRepository.findById(id)
                .map(Usuario::getActivo)
                .orElse(false);
    }

    private UsuarioResponseDTO toResponse(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .email(u.getEmail())
                .telefono(u.getTelefono())
                .rol(u.getRol())
                .activo(u.getActivo())
                .creadoEn(u.getCreadoEn())
                .build();
    }
}