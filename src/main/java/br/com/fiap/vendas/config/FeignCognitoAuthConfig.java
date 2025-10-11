package br.com.fiap.vendas.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Configuration
public class FeignCognitoAuthConfig implements RequestInterceptor{

    @Override
    public void apply(RequestTemplate template) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            String token = jwt.getTokenValue();
            template.header("Authorization", "Bearer " + token);
        } else {
            System.out.println("Nenhum token JWT encontrado no contexto.");
        }
    }
}
