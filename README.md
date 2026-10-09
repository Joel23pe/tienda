# Tienda – API REST con Spring Data JPA y JWT

Avance de Proyecto Final 2 (APF2) – Desarrollo Web Integrado (100000ST62), Sección 60618.

Back-end de una tienda (productos, categorías, ventas y usuarios) con persistencia en
**Spring Data JPA / Hibernate**, consultas **JPQL**, transacciones con `@Transactional` y
seguridad con **Spring Security + JWT**.

## Tecnologías
Java 21 · Spring Boot 4.1.1 · Spring Data JPA (Hibernate 7) · H2 (en memoria) ·
Spring Security · jjwt 0.12.6 · Maven

## Estructura de paquetes (`com.apf2.tienda`)
| Paquete | Contenido |
|---|---|
| `model` | Entidades JPA: `Categoria`, `Producto`, `Venta`, `Usuario`, `Rol` |
| `repository` | Interfaces `JpaRepository` y consultas JPQL (`ProductoRepository`) |
| `service` | Lógica de negocio; `VentaService` usa `@Transactional` |
| `controller` | Endpoints REST |
| `security` | `SecurityConfig`, `JwtUtil`, `JwtAuthFilter`, `UserDetailsServiceImpl` |
| `exception` | Manejo global de errores |
| `config` | `DataInitializer` (datos de prueba) |

## Cómo ejecutar
1. Clonar el repositorio: `git clone https://github.com/Joel23pe/tienda.git`
2. Verificar en `src/main/resources/application.properties` que exista una clave Base64
   en `app.jwt.secret` (mínimo 256 bits). Para generar una en PowerShell:
   ```powershell
   [Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
   ```
3. Ejecutar desde VS Code (clase `TiendaApplication`) o con Maven:
   ```
   .\mvnw.cmd spring-boot:run
   ```
   Debe aparecer `Started TiendaApplication` en la consola. La base H2 no requiere instalación.
4. Opcional, consola de H2: http://localhost:8080/h2-console
   (JDBC URL `jdbc:h2:mem:tiendadb`, usuario `sa`, contraseña vacía).

## Usuarios de prueba
| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | `ROLE_ADMIN` (puede usar DELETE) |
| `user` | `user123` | `ROLE_USER` |

## Endpoints
| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/auth/login` | Público |
| POST | `/api/auth/register` | Público |
| GET / POST | `/api/productos` | Token JWT |
| GET / PUT | `/api/productos/{id}` | Token JWT |
| DELETE | `/api/productos/{id}` | Solo ADMIN |
| GET | `/api/productos/buscar?texto=` | Token JWT (JPQL) |
| GET | `/api/productos/categoria/{id}` | Token JWT (JPQL) |
| GET | `/api/productos/stock-bajo?minimo=` | Token JWT (JPQL) |
| GET | `/api/productos/valor-inventario` | Token JWT (JPQL) |
| GET / POST | `/api/categorias` | Token JWT |
| POST / GET | `/api/ventas` | Token JWT (transaccional) |

Sin token la API responde `401`; con token pero sin permiso, `403`.

## Pruebas con Postman
Importar `postman/Tienda-APF2.postman_collection.json`. Ejecutar primero
**1. Auth → Login admin**: guarda el token automáticamente en la variable `token`.
El token dura 1 hora y deja de ser válido si se cambia `app.jwt.secret`; en ese caso hay que
iniciar sesión otra vez. Los datos son en memoria: al reiniciar la aplicación vuelven al estado inicial.