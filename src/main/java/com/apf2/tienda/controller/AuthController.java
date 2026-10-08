package com.apf2.tienda.controller;

import com.apf2.tienda.model.Rol;
import com.apf2.tienda.model.Usuario;
import com.apf2.tienda.repository.UsuarioRepository;
import com.apf2.tienda.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * CAPÍTULO 3 - 3.2 IMPLEMENTACIÓN DE JWT
 * Rutas PÚBLICAS (/api/auth/**): registro y login. El login devuelve el token firmado.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record RegisterRequest(@NotBlank String username, @NotBlank @Size(min = 6) String password) {}

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, UsuarioRepository usuarioRepo,
                          PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.usuarioRepo = usuarioRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest req) {
        if (usuarioRepo.existsByUsername(req.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "El usuario ya existe"));
        }
        // Todo usuario registrado públicamente recibe ROLE_USER (el ADMIN solo se crea por el sistema)
        usuarioRepo.save(new Usuario(req.username(), encoder.encode(req.password()), Rol.ROLE_USER));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Usuario registrado"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            // Valida usuario/contraseña contra la BD (UserDetailsServiceImpl + BCrypt)
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password()));
            String token = jwtUtil.generarToken(auth.getName(),
                    auth.getAuthorities().iterator().next().getAuthority());
            return ResponseEntity.ok(Map.of("token", token, "tipo", "Bearer", "usuario", auth.getName()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
    }
}
