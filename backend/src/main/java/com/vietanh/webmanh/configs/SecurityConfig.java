package com.vietanh.webmanh.configs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableTransactionManagement
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.frontend.base-url}")
    String FE_DOMAIN;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/",
            "/actuator/**",
            "/error-report",
            "/comics/**",
            "/genres",
            "/auth/**",
            "/mail/**"
    };

    // ========= AUTHENTICATED (không phân role) =========
    private static final String[] AUTHENTICATED_ENDPOINTS = {
            "/view/increase/**"
    };

    // ========= ROLE BASE =========
    private static final String[] ADMIN_ENDPOINTS = {
            "/admin/**"
    };

    private static final String[] MANAGEMENT_ENDPOINTS = {
            "/management/**"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults());

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Admin
                        .requestMatchers(ADMIN_ENDPOINTS)
                        .hasAuthority("ROLE_ADMIN")

                        // Management (USER + ADMIN)
                        .requestMatchers(MANAGEMENT_ENDPOINTS)
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_AUTHOR")
                        // hasAnyRole không cần prefix ROLE_

                        // authenticated
                        .requestMatchers(AUTHENTICATED_ENDPOINTS)
                        .authenticated()

                        // Public
                        .requestMatchers(PUBLIC_ENDPOINTS)
                        .permitAll()

                        .anyRequest()
                        .permitAll()
                );

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(FE_DOMAIN));
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

