package com.tuapp.msnotificaciones.repository;

import com.tuapp.msnotificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioId(Long usuarioId);
    List<Notificacion> findByEstado(String estado);
    List<Notificacion> findByTipo(String tipo);
    List<Notificacion> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}