package com.apf2.tienda.repository;

import com.apf2.tienda.model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * CAPÍTULO 1 - 1.3 REPOSITORIOS  +  CAPÍTULO 2 - 2.1 CONSULTAS CON JPQL
 * JPQL trabaja con clases y atributos Java (Producto, p.nombre), NO con tablas/columnas SQL.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // CONCURRENCIA: bloqueo pesimista de escritura (SELECT ... FOR UPDATE en PostgreSQL).
    // Otra transacción que intente bloquear la misma fila espera hasta que esta termine.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);

    // JPQL #1: filtra por la relación @ManyToOne y ordena por nombre
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId ORDER BY p.nombre")
    List<Producto> buscarPorCategoria(@Param("categoriaId") Long categoriaId);

    // JPQL #2: búsqueda por texto, sin distinguir mayúsculas/minúsculas
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Producto> buscarPorNombre(@Param("texto") String texto);

    // JPQL #3: productos con stock por debajo de un mínimo (reposición)
    @Query("SELECT p FROM Producto p WHERE p.stock < :minimo ORDER BY p.stock ASC")
    List<Producto> stockBajo(@Param("minimo") int minimo);

    // JPQL #4: valor total del inventario (consulta de agregación)
    @Query("SELECT COALESCE(SUM(p.precio * p.stock), 0) FROM Producto p")
    BigDecimal valorTotalInventario();

    // Método derivado (alternativa a @Query): Spring genera la consulta a partir del nombre
    List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);
}