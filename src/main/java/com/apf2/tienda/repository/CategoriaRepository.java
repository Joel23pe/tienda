package com.apf2.tienda.repository;

import com.apf2.tienda.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CAPÍTULO 1 - 1.3 REPOSITORIOS
 * Al extender JpaRepository se obtiene CRUD automático (save, findAll, findById, deleteById...)
 * sin escribir SQL.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
