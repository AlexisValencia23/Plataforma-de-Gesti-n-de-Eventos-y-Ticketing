package com.tuapp.mspagos.repository;

import com.tuapp.mspagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByUsuarioId(Long usuarioId);
    List<Pago> findByTicketId(Long ticketId);
    List<Pago> findByEstado(String estado);
    Optional<Pago> findByTicketIdAndEstado(Long ticketId, String estado);
}