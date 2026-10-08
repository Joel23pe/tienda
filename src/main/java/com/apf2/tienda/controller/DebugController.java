package com.apf2.tienda.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.LinkedHashMap;
import java.util.Map;

/** TEMPORAL: diagnostica por qué se rechaza un token. Borrar antes de entregar. */
@RestController
@RequestMapping("/api/auth")
public class DebugController {

    private final SecretKey key;

    public DebugController(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @GetMapping("/debug")
    public Map<String, String> debug(@RequestHeader(value = "Authorization", required = false) String header) {
        Map<String, String> r = new LinkedHashMap<>();
        if (header == null) {
            r.put("resultado", "NO LLEGO LA CABECERA Authorization");
            return r;
        }
        String token = header.startsWith("Bearer ") ? header.substring(7).trim() : header.trim();
        try {
            var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            r.put("resultado", "TOKEN VALIDO");
            r.put("usuario", claims.getSubject());
            r.put("expira", String.valueOf(claims.getExpiration()));
        } catch (Throwable e) {
            r.put("resultado", "TOKEN RECHAZADO");
            r.put("tipo", e.getClass().getName());
            r.put("mensaje", String.valueOf(e.getMessage()));
        }
        return r;
    }
}