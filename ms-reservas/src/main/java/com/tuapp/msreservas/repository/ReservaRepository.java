package com.tuapp.msreservas.repository;

import com.tuapp.msreservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);
    List<Reserva> findByEventoId(Long eventoId);
    List<Reserva> findByEstado(String estado);
    List<Reserva> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}