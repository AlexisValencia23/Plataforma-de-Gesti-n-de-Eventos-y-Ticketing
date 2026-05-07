package com.tuapp.mscategorias.repository;

import com.tuapp.mscategorias.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByActivoTrue();
    Optional<Categoria> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}