package com.apf2.tienda.config;

import com.apf2.tienda.model.*;
import com.apf2.tienda.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

/**
 * Carga datos de prueba al iniciar (solo si las tablas están vacías).
 * Sirve como evidencia del Capítulo 1: tablas creadas por Hibernate con registros.
 * Usuarios: admin / admin123 (ROLE_ADMIN)  y  user / user123 (ROLE_USER)
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatos(UsuarioRepository usuarios, CategoriaRepository categorias,
                                  ProductoRepository productos, PasswordEncoder encoder) {
        return args -> {
            if (usuarios.count() == 0) {
                usuarios.save(new Usuario("admin", encoder.encode("admin123"), Rol.ROLE_ADMIN));
                usuarios.save(new Usuario("user", encoder.encode("user123"), Rol.ROLE_USER));
            }
            if (categorias.count() == 0) {
                Categoria tecno = categorias.save(new Categoria("Tecnología"));
                Categoria hogar = categorias.save(new Categoria("Hogar"));
                productos.save(new Producto("Laptop", new BigDecimal("2500.00"), 15, tecno));
                productos.save(new Producto("Mouse inalámbrico", new BigDecimal("45.90"), 4, tecno));
                productos.save(new Producto("Licuadora", new BigDecimal("180.00"), 8, hogar));
            }
        };
    }
}
