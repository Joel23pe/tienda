package com.apf2.tienda.controller;

import com.apf2.tienda.model.Venta;
import com.apf2.tienda.service.VentaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Proceso transaccional (Capítulo 2 - 2.3) expuesto como endpoint. */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    public record VentaRequest(@NotNull Long productoId, @Min(1) int cantidad) {}

    private final VentaService service;

    public VentaController(VentaService service) { this.service = service; }

    // Authentication contiene al usuario autenticado por el filtro JWT
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta registrar(@Valid @RequestBody VentaRequest req, Authentication auth) {
        return service.registrarVenta(req.productoId(), req.cantidad(), auth.getName());
    }

    @GetMapping
    public List<Venta> listar() { return service.listar(); }
}
