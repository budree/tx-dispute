package com.example.txd.controller;

import com.example.txd.controller.api.v1.DisputesApi;
import com.example.txd.dto.DisputeResponse;
import com.example.txd.model.DisputeStatus;
import com.example.txd.service.DisputeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DisputesApi.class)
class DisputesApiWebTest {

    @Autowired MockMvc mvc;

    @MockBean DisputeService service;
    // Required bean for resource server in WebMvcTest
    @MockBean JwtDecoder jwtDecoder;

    @Test
    void list_asAdmin_returnsData() throws Exception {
        var d = new DisputeResponse(
            1L, "TXN-1", "Reason", DisputeStatus.OPEN,
            Instant.now(), null, Instant.now(), null, null, null,
            1L, "admin"
        );
        when(service.list()).thenReturn(List.of(d));

        mvc.perform(get("/api/v1/disputes").with(jwt().jwt(j -> j.claim("role","ADMIN"))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].transactionRef").value("TXN-1"))
           .andExpect(jsonPath("$[0].status").value("OPEN"));
    }
}
