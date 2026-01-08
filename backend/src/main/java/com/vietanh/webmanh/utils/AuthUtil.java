package com.vietanh.webmanh.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;
import java.util.stream.Collectors;

public class AuthUtil {
    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return Integer.parseInt(jwt.getSubject()); // userId là subject của JWT
    }

    public static Set<String> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Set.of();
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public static Set<String> getCurrentUserRoles() {
        return getCurrentUserAuthorities()
                .stream()
                .filter(a -> a.startsWith("ROLE_"))
                .collect(Collectors.toSet());
    }

    public static boolean hasRole(String role) {
        return getCurrentUserAuthorities().contains("ROLE_" + role);
    }
}
