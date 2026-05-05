package com.tuapp.mstickets.repository;

import com.tuapp.mstickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUsuarioId(Long usuarioId);
    List<Ticket> findByEventoId(Long eventoId);
    List<Ticket> findByEstado(String estado);
    List<Ticket> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}