package br.com.fiap.vendas.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.logging.Logger;

@Configuration
public class FeignCognitoAuthConfig implements RequestInterceptor{

    Logger logger = Logger.getLogger(FeignCognitoAuthConfig.class.getName());

    @Override
    public void apply(RequestTemplate template) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            String token = jwt.getTokenValue();
            template.header("Authorization", "Bearer " + token);
        } else {
            logger.severe("Nenhum token JWT encontrado no contexto.");
        }
    }
}
