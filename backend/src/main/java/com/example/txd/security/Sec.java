package com.example.txd.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class Sec {
    public static Long userId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return null;
        Object p = a.getPrincipal();
        if (p instanceof Jwt jwt) return jwt.getClaim("userId");
        return null;
    }
    public static String role() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return null;
        if (a.getPrincipal() instanceof Jwt jwt) return jwt.getClaim("role");
        return null;
    }
    private Sec() {}
}
