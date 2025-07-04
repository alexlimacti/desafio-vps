package com.vps.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vps.desafio.dto.ParceiroRequestDTO;
import com.vps.desafio.dto.ParceiroResponseDTO;
import com.vps.desafio.service.ParceiroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParceiroController.class)
class ParceiroControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ParceiroService parceiroService;
    @Autowired
    private ObjectMapper objectMapper;

    private ParceiroResponseDTO parceiroResponseDTO;

    @BeforeEach
    void setUp() {
        parceiroResponseDTO = new ParceiroResponseDTO(
                1L,
                "Empresa XPTO",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00")
        );
    }

    @Test
    void cadastrarParceiro_sucesso() throws Exception {
        ParceiroRequestDTO request = new ParceiroRequestDTO("Empresa XPTO", new BigDecimal("1000.00"));
        Mockito.when(parceiroService.cadastrar(any())).thenReturn(parceiroResponseDTO);
        mockMvc.perform(post("/api/parceiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Empresa XPTO"))
                .andExpect(jsonPath("$.limiteCredito").value(1000.00))
                .andExpect(jsonPath("$.creditoDisponivel").value(1000.00));
    }
}

