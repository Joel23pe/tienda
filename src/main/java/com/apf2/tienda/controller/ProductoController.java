package com.apf2.tienda.controller;

import com.apf2.tienda.model.Producto;
import com.apf2.tienda.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** CAPÍTULO 4 - Endpoints a probar en Postman: CRUD (POST/GET/PUT/DELETE) y consultas JPQL. */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) { this.service = service; }

    // ---------- CRUD ----------
    @GetMapping
    public List<Producto> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody Producto p) { return service.crear(p); }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody Producto p) {
        return service.actualizar(id, p);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }

    // ---------- Consultas JPQL (Capítulo 2 - 2.2) ----------
    @GetMapping("/categoria/{categoriaId}")
    public List<Producto> porCategoria(@PathVariable Long categoriaId) {
        return service.porCategoria(categoriaId);
    }

    @GetMapping("/buscar")
    public List<Producto> buscar(@RequestParam String texto) { return service.buscar(texto); }

    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo(@RequestParam(defaultValue = "10") int minimo) {
        return service.stockBajo(minimo);
    }

    @GetMapping("/valor-inventario")
    public Map<String, BigDecimal> valorInventario() {
        return Map.of("valorTotal", service.valorInventario());
    }
}
