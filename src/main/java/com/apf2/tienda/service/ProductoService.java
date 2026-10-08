package com.apf2.tienda.service;

import com.apf2.tienda.exception.RecursoNoEncontradoException;
import com.apf2.tienda.model.Producto;
import com.apf2.tienda.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** CAPÍTULO 2 - 2.1 LÓGICA DE NEGOCIO Y SERVICIOS (@Service) - CRUD de productos */
@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository repo, CategoriaService categoriaService) {
        this.repo = repo;
        this.categoriaService = categoriaService;
    }

    // Consultas de solo lectura: readOnly = true optimiza la transacción
    @Transactional(readOnly = true)
    public List<Producto> listar() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Producto obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + id + " no existe"));
    }

    // CREATE
    @Transactional
    public Producto crear(Producto p) {
        p.setId(null);
        asignarCategoria(p);
        return repo.save(p);
    }

    // UPDATE
    @Transactional
    public Producto actualizar(Long id, Producto datos) {
        Producto p = obtener(id);
        p.setNombre(datos.getNombre());
        p.setPrecio(datos.getPrecio());
        p.setStock(datos.getStock());
        p.setCategoria(datos.getCategoria());
        asignarCategoria(p);
        return repo.save(p);
    }

    // DELETE
    @Transactional
    public void eliminar(Long id) { repo.delete(obtener(id)); }

    // ---- Uso de las consultas JPQL definidas en el repositorio (2.2) ----
    public List<Producto> porCategoria(Long categoriaId) { return repo.buscarPorCategoria(categoriaId); }
    public List<Producto> buscar(String texto) { return repo.buscarPorNombre(texto); }
    public List<Producto> stockBajo(int minimo) { return repo.stockBajo(minimo); }
    public BigDecimal valorInventario() { return repo.valorTotalInventario(); }

    // Valida que la categoría enviada exista (si se envió una)
    private void asignarCategoria(Producto p) {
        if (p.getCategoria() != null && p.getCategoria().getId() != null) {
            p.setCategoria(categoriaService.obtener(p.getCategoria().getId()));
        }
    }
}
