package br.com.fiap.vendas.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FeignCognitoAuthConfigTest {

    private FeignCognitoAuthConfig config;

    @BeforeEach
    void setUp() {
        config = new FeignCognitoAuthConfig();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAdicionarHeaderAuthorizationQuandoJwtPresente() {
        // Arrange
        Jwt jwt = new Jwt(
                "fake-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user123")
        );

        var authentication = new UsernamePasswordAuthenticationToken("user", jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RequestTemplate template = new RequestTemplate();

        // Act
        config.apply(template);

        // Assert
        assertTrue(template.headers().containsKey("Authorization"));
        assertEquals("Bearer fake-token", template.headers().get("Authorization").iterator().next());
    }

    @Test
    void naoDeveAdicionarHeaderQuandoNaoHaJwt() {
        // Arrange
        var authentication = new UsernamePasswordAuthenticationToken("user", "senha");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RequestTemplate template = new RequestTemplate();

        // Act
        config.apply(template);

        // Assert
        assertFalse(template.headers().containsKey("Authorization"));
    }

    @Test
    void naoDeveLancarExcecaoQuandoNaoHaAuthentication() {
        // Arrange
        SecurityContextHolder.clearContext();
        RequestTemplate template = new RequestTemplate();

        // Act + Assert
        assertDoesNotThrow(() -> config.apply(template));
    }
}
