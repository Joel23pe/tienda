package com.apf2.tienda.repository;

import com.apf2.tienda.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** CAPÍTULO 1 - 1.3 REPOSITORIOS (método derivado: Spring genera la consulta por el nombre). */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
}
