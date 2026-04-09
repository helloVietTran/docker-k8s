package com.viettran.reading_story_web.config;

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
    @Value("${app.frontend.url}")
    String FRONTEND_URL;

    private final String[] GET_METHOD_PUBLIC_ENDPOINTS = {
        "/shop/avatar-frame",
        "/stories/{storyId}/all-chapters",
        "/stories/{storyId}/chapters",
        "/stories/chapters/{chapterId}",
        "/genres",
        "/stories",
        "/stories/{storyId}",
        "/stories/search",
        "/stories/top-views",
        "/stories/find-story",
        "/stories/{storyId}/increase-view",
        "/stories/gender",
        "/stories/hot",
        "/stories/find-advanced",
        "/stories/{storyId}/all-chapters",
        "/stories/{storyId}/chapters",
        "/stories/chapters/{chapterId}",
        "/stories/{storyId}/chap",
        "/stories/featured-stories",
        "/users/{userId}",
        "/users/top-user",
        "/stories/top-views",
        "/stories/chapters/{chapterId}/resource",
        "/comments/stories/{storyId}",
        "/comments/chapters/{chapterId}",
        "/comments/new-comments",
        "/level/users/{userId}"
    };
    private final String[] POST_METHOD_PUBLIC_ENDPOINTS = {
        "/auth/login",
        "/auth/introspect",
        "/auth/refresh",
        "/auth/logout",
        "/email/send",
        "/error-reporter",
        "/users/register",
        "/users/forgot-password",
        "/avatar-frame",
        "/stories",
    };
    private final String[] PATCH_METHOD_PUBLIC_ENDPOINTS = {
        "/stories/{storyId}/chapters/{chapterId}/increase-view", "/stories/{storyId}/rate", "/users/reset-password"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(Customizer.withDefaults());

        httpSecurity
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, POST_METHOD_PUBLIC_ENDPOINTS)
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, GET_METHOD_PUBLIC_ENDPOINTS)
                        .permitAll()
                        .requestMatchers(HttpMethod.PATCH, PATCH_METHOD_PUBLIC_ENDPOINTS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(Customizer.withDefaults());

        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

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
        corsConfiguration.setAllowedOriginPatterns(List.of(FRONTEND_URL));
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
