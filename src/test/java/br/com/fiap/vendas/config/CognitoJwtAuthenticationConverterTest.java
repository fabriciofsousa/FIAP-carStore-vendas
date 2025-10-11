package br.com.fiap.vendas.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CognitoJwtAuthenticationConverterTest {

    private final CognitoJwtAuthenticationConverter converter = new CognitoJwtAuthenticationConverter();

    @Test
    void deveConverterJwtComGruposEmAuthorities() {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user123", "cognito:groups", List.of("admin", "user"))
        );

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertNotNull(token);
        assertEquals(2, token.getAuthorities().size());
        assertTrue(token.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
        assertTrue(token.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER")));
    }

    @Test
    void deveRetornarAuthoritiesVazioQuandoSemGrupos() {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user123")
        );

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertNotNull(token);
        assertTrue(token.getAuthorities().isEmpty());
    }
}
