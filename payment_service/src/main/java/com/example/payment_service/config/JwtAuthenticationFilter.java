package com.example.payment_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        // Cho phép webhook không cần JWT
        if (path.startsWith("/api/webhook")) {
            chain.doFilter(req, res);
            return;
        }

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(auth.substring(7))
                    .getBody();

            var authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    null
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ignored) {
        }

        chain.doFilter(req, res);
    }
}

