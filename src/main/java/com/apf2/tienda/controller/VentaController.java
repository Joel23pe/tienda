package com.apf2.tienda.controller;

import com.apf2.tienda.dto.VentaRequest;
import com.apf2.tienda.model.Venta;
import com.apf2.tienda.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Proceso transaccional maestro-detalle expuesto como endpoint. */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService service;

    public VentaController(VentaService service) { this.service = service; }

    // Authentication contiene al usuario autenticado por el filtro JWT
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta registrar(@Valid @RequestBody VentaRequest req, Authentication auth) {
        return service.registrarVenta(req.items(), auth.getName());
    }

    @GetMapping
    public List<Venta> listar() { return service.listar(); }
}