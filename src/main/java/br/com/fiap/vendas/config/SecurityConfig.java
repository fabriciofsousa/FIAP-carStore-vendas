package br.com.fiap.vendas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CognitoJwtAuthenticationConverter cognitoJwtAuthConverter;

    public SecurityConfig(CognitoJwtAuthenticationConverter cognitoJwtAuthConverter) {
        this.cognitoJwtAuthConverter = cognitoJwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger-resources/**",
                                "/swagger-resources", "/configuration/ui", "/configuration/security",
                                "/webjars/**", "/favicon.ico", "/error").permitAll()

                        .requestMatchers(HttpMethod.POST, "/vendas/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/vendas/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/vendas/**").hasAnyAuthority("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(cognitoJwtAuthConverter)
                        )
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
