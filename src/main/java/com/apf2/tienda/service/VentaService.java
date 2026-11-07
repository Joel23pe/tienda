package com.apf2.tienda.service;

import com.apf2.tienda.dto.ItemVentaRequest;
import com.apf2.tienda.exception.RecursoNoEncontradoException;
import com.apf2.tienda.model.DetalleVenta;
import com.apf2.tienda.model.Producto;
import com.apf2.tienda.model.Venta;
import com.apf2.tienda.repository.ProductoRepository;
import com.apf2.tienda.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Registro transaccional de una venta con varios productos (maestro-detalle).
 * - ATOMICIDAD: todo (stock de N productos + cabecera + detalles) se guarda junto o se revierte.
 * - CONCURRENCIA: cada producto se bloquea con PESSIMISTIC_WRITE antes de leer su stock,
 *   así dos compras simultáneas de la última unidad no pueden vender ambas.
 * - Los productos se bloquean en orden de id (TreeMap) para evitar deadlocks entre ventas cruzadas.
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
    public Venta registrarVenta(List<ItemVentaRequest> items, String usuario) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto");
        }

        // Une líneas repetidas del mismo producto y las ordena por id
        Map<Long, Integer> cantidades = new TreeMap<>();
        for (ItemVentaRequest item : items) {
            if (item.cantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setUsuario(usuario);
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cantidades.entrySet()) {
            Long productoId = entry.getKey();
            int cantidad = entry.getValue();

            // Bloqueo pesimista: la fila queda bloqueada hasta el final de la transacción
            Producto p = productoRepo.findByIdForUpdate(productoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + productoId + " no existe"));

            if (p.getStock() < cantidad) {
                // RuntimeException -> rollback: no se modifica nada
                throw new IllegalStateException(
                        "Stock insuficiente para '" + p.getNombre() + "'. Disponible: " + p.getStock());
            }

            // Operación 1: descontar stock
            p.setStock(p.getStock() - cantidad);
            productoRepo.save(p);

            // Operación 2: crear la línea de detalle
            BigDecimal subtotal = p.getPrecio().multiply(BigDecimal.valueOf(cantidad));
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(p);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(p.getPrecio());
            detalle.setSubtotal(subtotal);
            venta.agregarDetalle(detalle);

            total = total.add(subtotal);
        }

        // Operación 3: guardar cabecera (los detalles se guardan por cascade)
        venta.setTotal(total);
        return ventaRepo.save(venta);
    }

    @Transactional(readOnly = true)
    public List<Venta> listar() { return ventaRepo.findAll(); }
}