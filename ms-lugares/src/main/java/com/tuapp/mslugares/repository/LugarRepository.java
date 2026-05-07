package com.tuapp.mslugares.repository;

import com.tuapp.mslugares.model.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, Long> {
    List<Lugar> findByActivoTrue();
    List<Lugar> findByCiudad(String ciudad);
    List<Lugar> findByCapacidadMaximaGreaterThanEqual(Integer capacidad);
}