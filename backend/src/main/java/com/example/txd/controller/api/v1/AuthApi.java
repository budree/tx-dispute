// src/main/java/com/example/txd/controller/api/AuthApi.java
package com.example.txd.controller.api.v1;

import com.example.txd.model.AppUser;
import com.example.txd.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthApi {

    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository users;

    public AuthApi(AuthenticationManager am, JwtEncoder enc, UserRepository users) {
        this.authManager = am; this.jwtEncoder = enc; this.users = users;
    }

    public record LoginRequest(String username, String password) {}
    public record TokenResponse(String token) {}
    public record MeResponse(Long id, String username, String role) {}

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        AppUser user = users.findByUsername(req.username()).orElseThrow();

        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .build();

        // *** IMPORTANT: Set HS256 explicitly ***
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        var params = JwtEncoderParameters.from(header, claims);

        String token = jwtEncoder.encode(params).getTokenValue();
        return new TokenResponse(token);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "unauthenticated"));
        }
        return ResponseEntity.ok(new MeResponse(
                jwt.getClaim("userId"),
                jwt.getSubject(),
                jwt.getClaim("role")
        ));
    }
}
