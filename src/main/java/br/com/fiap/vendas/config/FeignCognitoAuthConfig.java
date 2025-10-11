package br.com.fiap.vendas.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Configuration
public class FeignCognitoAuthConfig {

    @Value("${cognito.auth.url}")
    private String cognitoUrl;

    @Value("${cognito.client.id}")
    private String clientId;

    @Value("${cognito.username}")
    private String username;

    @Value("${cognito.password}")
    private String password;

    private String cachedToken;
    private long tokenExpirationTime = 0;

    @Bean
    public RequestInterceptor cognitoAuthInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                String token = getAccessToken();
                template.header("Authorization", "Bearer " + token);
            }
        };
    }

    private String getAccessToken() {
        long now = System.currentTimeMillis();
        if (cachedToken != null && now < tokenExpirationTime) {
            return cachedToken;
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/x-amz-json-1.1"));
        headers.set("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth");

        String body = "{\n" +
                "  \"AuthFlow\": \"USER_PASSWORD_AUTH\",\n" +
                "  \"ClientId\": \"" + clientId + "\",\n" +
                "  \"AuthParameters\": {\n" +
                "    \"USERNAME\": \"" + username + "\",\n" +
                "    \"PASSWORD\": \"" + password + "\"\n" +
                "  }\n" +
                "}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                cognitoUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map authResult = (Map) ((Map) response.getBody().get("AuthenticationResult"));
        String token = (String) authResult.get("AccessToken");
        Integer expiresIn = (Integer) authResult.get("ExpiresIn");

        cachedToken = token;
        tokenExpirationTime = now + (expiresIn - 60) * 1000L; // renova 1 min antes de expirar

        return token;
    }
}
