package com.apf2.tienda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * CAPÍTULO 1 - 1.2 MAPEO DE ENTIDADES
 * @Entity  -> la clase es una tabla; @Table -> nombre de la tabla
 * @Id + @GeneratedValue -> clave primaria autoincremental (AUTO_INCREMENT en MySQL)
 * @OneToMany -> una categoría tiene muchos productos (lado inverso de la relación)
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    // @JsonIgnore evita la recursión infinita al serializar (Categoria -> Producto -> Categoria)
    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Producto> productos = new ArrayList<>();

    public Categoria() {}

    public Categoria(String nombre) { this.nombre = nombre; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}
