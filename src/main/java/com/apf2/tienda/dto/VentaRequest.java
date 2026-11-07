package com.apf2.tienda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** Cuerpo del POST /api/ventas: una venta con varios productos. */
public record VentaRequest(@NotEmpty @Valid List<ItemVentaRequest> items) {}