package com.tuapp.mseventos.repository;

import com.tuapp.mseventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByEstado(String estado);
    List<Evento> findByCategoria(String categoria);
    List<Evento> findByOrganizadorId(Long organizadorId);
    List<Evento> findByEstadoAndCapacidadDisponibleGreaterThan(String estado, Integer capacidad);
}