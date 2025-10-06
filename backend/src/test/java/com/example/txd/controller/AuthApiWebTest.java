package com.example.txd.controller;

import com.example.txd.controller.api.v1.AuthApi;
import com.example.txd.model.AppUser;
import com.example.txd.model.Role;
import com.example.txd.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthApi.class)
@AutoConfigureMockMvc(addFilters = false) // <-- disable security filters for this slice
class AuthApiWebTest {

    @Autowired MockMvc mvc;

    @MockBean AuthenticationManager authManager;
    @MockBean JwtEncoder jwtEncoder;
    @MockBean UserRepository users;

    @Test
    void login_returnsToken() throws Exception {
        Authentication ok = new UsernamePasswordAuthenticationToken("admin", "x");
        Mockito.when(authManager.authenticate(any())).thenReturn(ok);

        var u = new AppUser(); u.setId(1L); u.setUsername("admin"); u.setRole(Role.ADMIN);
        Mockito.when(users.findByUsername("admin")).thenReturn(Optional.of(u));

        Instant now = Instant.now();
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg","HS256")
                .subject("admin")
                .claim("userId", 1L)
                .claim("role", "ADMIN")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .build();
        Mockito.when(jwtEncoder.encode(any())).thenReturn(jwt);

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void me_unauthenticated_is401() throws Exception {
        // Your controller returns 401 when jwt == null, so this still holds even with filters off
        mvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
