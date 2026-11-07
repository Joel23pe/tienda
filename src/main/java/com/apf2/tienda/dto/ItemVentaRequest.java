package com.apf2.tienda.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Una línea solicitada: qué producto y cuántas unidades. */
public record ItemVentaRequest(@NotNull Long productoId, @Min(1) int cantidad) {}