package com.apf2.tienda.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * CAPÍTULO 3 - GENERACIÓN Y VALIDACIÓN DEL TOKEN
 * Clase utilitaria JWT: firma con SecretKey, tiempo de expiración y lectura de claims.
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    /** Genera el token: sujeto = username, claim "rol", fecha de emisión y expiración, firmado con la clave. */
    public String generarToken(String username, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** Valida firma y expiración. Imprime el motivo si el token es rechazado. */
    public boolean esValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (Throwable e) {
            System.out.println(">>> JWT RECHAZADO: " + e.getClass().getName() + " - " + e.getMessage());
            return false;
        }
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}