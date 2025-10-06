// src/main/java/com/example/txd/config/SecurityConfig.java
package com.example.txd.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.*;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        var key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        // Make decoding expect HS256
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        // ImmutableSecret -> HMAC (HS*) encoder
        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(jwtSecret.getBytes(StandardCharsets.UTF_8)));
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        var conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(jwt ->
                org.springframework.security.core.authority.AuthorityUtils.createAuthorityList(
                        "ROLE_" + String.valueOf(jwt.getClaims().getOrDefault("role", "CLIENT"))
                )
        );
        return conv;
    }

    @Bean
    @Order(1) // evaluated before the main chain
    // @ConditionalOnProperty(name = "app.swagger.bypass", havingValue = "true")
    SecurityFilterChain swaggerBypassChain(HttpSecurity http) throws Exception {
        RequestMatcher fromSwaggerUi = request -> {
            String ref = request.getHeader("Referer");
            return ref != null && ref.contains("/swagger-ui");
        };
        RequestMatcher apiPaths = new AntPathRequestMatcher("/api/**");
        RequestMatcher matcher = new AndRequestMatcher(apiPaths, fromSwaggerUi);

        http.securityMatcher(matcher);
        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        http.oauth2ResourceServer(oauth -> oauth.disable()); // no JWT required for these
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtConv) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()

                // Disputes
                .requestMatchers(HttpMethod.GET, "/api/v1/disputes/**").hasAnyRole("ADMIN","CLIENT")   // view
                .requestMatchers(HttpMethod.POST, "/api/v1/disputes").hasAnyRole("ADMIN","CLIENT")     // create
                .requestMatchers(HttpMethod.POST, "/api/v1/disputes/*:advance").hasRole("ADMIN")       // admin-only
                .requestMatchers("/api/v1/disputes/**").hasRole("ADMIN")                               // everything else

                // Users
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                // Transactions
                .requestMatchers("/api/v1/transactions/**").hasAnyRole("ADMIN","CLIENT")

                .anyRequest().authenticated()
        );
        http.oauth2ResourceServer(oauth -> oauth.jwt(j -> j.jwtAuthenticationConverter(jwtConv)));
        return http.build();
    }
}
