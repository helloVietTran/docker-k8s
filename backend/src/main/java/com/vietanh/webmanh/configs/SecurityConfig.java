package com.vietanh.webmanh.configs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.experimental.NonFinal;

@Configuration
@EnableTransactionManagement
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @NonFinal
    @Value("${app.frontend.base-url}")
    String FE_DOMAIN;

    private final String[] GET_METHOD_PUBLIC_ENDPOINTS = {
            "/", "/actuator/**", "/error-report", "/comics/search", "/comics/{comicId}",
            "/genres",
            "/comics/chapters/{chapterId}"
    };
    private final String[] POST_METHOD_PUBLIC_ENDPOINTS = {
            "/auth/register", "/auth/login", "/auth/refresh", "/auth/introspect",
            "/auth/forget-password",
            "/mail/send-verify-mail",
            "/error-report"
    };
    private final String[] PUT_METHOD_PUBLIC_ENDPOINTS = {
            "/auth/change-password", "/auth/change-password/reset-token",
            "/auth/verify-account",
            "/views/chapters/{chapterId}"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(Customizer.withDefaults());

        httpSecurity
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, GET_METHOD_PUBLIC_ENDPOINTS)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, POST_METHOD_PUBLIC_ENDPOINTS)
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT, PUT_METHOD_PUBLIC_ENDPOINTS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(AbstractHttpConfigurer::disable);

        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        httpSecurity.csrf(AbstractHttpConfigurer::disable); //csrf dựa trên session

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of(FE_DOMAIN));
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
