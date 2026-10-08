package com.apf2.tienda.service;

import com.apf2.tienda.exception.RecursoNoEncontradoException;
import com.apf2.tienda.model.Categoria;
import com.apf2.tienda.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** CAPÍTULO 2 - 2.1 LÓGICA DE NEGOCIO Y SERVICIOS (@Service) */
@Service
public class CategoriaService {

    private final CategoriaRepository repo;

    public CategoriaService(CategoriaRepository repo) {
        this.repo = repo;
    }

    public List<Categoria> listar() { return repo.findAll(); }

    public Categoria obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría " + id + " no existe"));
    }

    public Categoria crear(Categoria c) {
        c.setId(null);
        return repo.save(c);
    }

    public Categoria actualizar(Long id, Categoria datos) {
        Categoria c = obtener(id);
        c.setNombre(datos.getNombre());
        return repo.save(c);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }
}
