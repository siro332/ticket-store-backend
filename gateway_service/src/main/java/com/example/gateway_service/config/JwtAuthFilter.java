package com.example.gateway_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GatewayFilter {

    @Value("${jwt.secret}")
    private String secret;

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            List roles = claims.get("roles", List.class);

            // Thêm thông tin user vào header
            exchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Email", email)
                            .header("X-User-Roles", roles.toString())
                            .build())
                    .build();

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}

