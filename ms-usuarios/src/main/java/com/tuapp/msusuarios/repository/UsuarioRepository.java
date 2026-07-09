package com.tuapp.msusuarios.repository;

import com.tuapp.msusuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(String rol);
    List<Usuario> findByActivoTrue();
    boolean existsByEmail(String email);
}