package com.example.txd.controller;

import com.example.txd.controller.api.v1.TransactionsApi;
import com.example.txd.dto.TransactionResponse;
import com.example.txd.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionsApi.class)
class TransactionsApiWebTest {

    @Autowired MockMvc mvc;

    @MockBean TransactionService service;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    void list_asClient_ok() throws Exception {
        var t = new TransactionResponse(1L, "TXN-1", new BigDecimal("9.99"), "ZAR", "desc", Instant.now());
        when(service.list()).thenReturn(List.of(t));

        mvc.perform(get("/api/v1/transactions")
                .with(jwt().jwt(j -> j.subject("client").claim("role","CLIENT").claim("userId", 5))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].reference").value("TXN-1"));
    }
}
