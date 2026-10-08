package com.apf2.tienda.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CAPÍTULO 3 - FILTRO DE AUTENTICACIÓN (OncePerRequestFilter)
 * Intercepta cada petición, lee "Authorization: Bearer <token>", valida el token
 * y registra al usuario autenticado en el SecurityContext.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println(">>> FILTRO: " + request.getMethod() + " " + request.getRequestURI()
                + " | Authorization = " + (header == null ? "NO LLEGO"
                : header.substring(0, Math.min(25, header.length())) + "..."));

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();

            try {
                if (jwtUtil.esValido(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String username = jwtUtil.extraerUsername(token);
                    UserDetails user = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println(">>> FILTRO: usuario autenticado = " + username);
                }
            } catch (Exception e) {
                System.out.println(">>> FILTRO ERROR: " + e.getClass().getName() + " - " + e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}