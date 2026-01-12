package ru.leti.wise.task.event.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfiguration {
    @Value("${spring.security.jwt.public-key}")
    private RSAPublicKey publicKey;

    @Bean
    public ReactiveJwtDecoder jwtDecoder(){
        return NimbusReactiveJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Order(1)
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> {
                            return ServerResponse.status(401)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of(
                                            "error", "Unauthorized",
                                            "message", ex.getMessage(),
                                            "timestamp", Instant.now().toString()
                                    ))
                                    .then();
                        })
                )
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }



    @Bean
    @Order(2)
    public SecurityWebFilterChain grpcSecurity(ServerHttpSecurity http) {
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/grpc/**"))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .build();
    }


}
