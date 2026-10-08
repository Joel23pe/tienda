package com.apf2.tienda.service;

import com.apf2.tienda.exception.RecursoNoEncontradoException;
import com.apf2.tienda.model.Producto;
import com.apf2.tienda.model.Venta;
import com.apf2.tienda.repository.ProductoRepository;
import com.apf2.tienda.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CAPÍTULO 2 - 2.3 MANEJO DE TRANSACCIONES (@Transactional)
 * Registrar una venta involucra DOS escrituras: descontar stock (productos) e insertar la venta (ventas).
 * Ambas deben cumplirse juntas o ninguna (ATOMICIDAD). Si ocurre una RuntimeException,
 * Spring hace ROLLBACK automático y la base de datos queda como estaba.
 */
@Service
public class VentaService {

    private final VentaRepository ventaRepo;
    private final ProductoRepository productoRepo;

    public VentaService(VentaRepository ventaRepo, ProductoRepository productoRepo) {
        this.ventaRepo = ventaRepo;
        this.productoRepo = productoRepo;
    }

    @Transactional
    public Venta registrarVenta(Long productoId, int cantidad, String usuario) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Producto p = productoRepo.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + productoId + " no existe"));

        if (p.getStock() < cantidad) {
            // RuntimeException -> rollback: no se modifica nada
            throw new IllegalStateException("Stock insuficiente. Disponible: " + p.getStock());
        }

        // Operación 1: descontar stock
        p.setStock(p.getStock() - cantidad);
        productoRepo.save(p);

        // Operación 2: registrar la venta
        Venta v = new Venta();
        v.setProducto(p);
        v.setCantidad(cantidad);
        v.setTotal(p.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
        v.setFecha(LocalDateTime.now());
        v.setUsuario(usuario);
        return ventaRepo.save(v);
    }

    @Transactional(readOnly = true)
    public List<Venta> listar() { return ventaRepo.findAll(); }
}
