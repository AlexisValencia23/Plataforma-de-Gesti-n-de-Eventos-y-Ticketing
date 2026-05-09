package com.tuapp.msreportes.repository;

import com.tuapp.msreportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByTipo(String tipo);
    List<Reporte> findByEstado(String estado);
    List<Reporte> findByGeneradoPor(Long usuarioId);
}