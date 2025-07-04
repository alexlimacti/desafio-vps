package com.vps.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vps.desafio.dto.ItemPedidoDTO;
import com.vps.desafio.dto.PedidoRequestDTO;
import com.vps.desafio.dto.PedidoResponseDTO;
import com.vps.desafio.model.StatusPedido;
import com.vps.desafio.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PedidoController.class)
class PedidoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PedidoService pedidoService;
    @Autowired
    private ObjectMapper objectMapper;

    private PedidoResponseDTO pedidoResponseDTO;

    @BeforeEach
    void setUp() {
        pedidoResponseDTO = new PedidoResponseDTO(
                1L,
                1L,
                List.of(new ItemPedidoDTO("Produto A", 2, new BigDecimal("100.00"))),
                new BigDecimal("200.00"),
                StatusPedido.PENDENTE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void criarPedido() throws Exception {
        PedidoRequestDTO request = new PedidoRequestDTO(1L, List.of(new ItemPedidoDTO("Produto A", 2, new BigDecimal("100.00"))));
        Mockito.when(pedidoService.criarPedido(any())).thenReturn(pedidoResponseDTO);
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void buscarPorId() throws Exception {
        Mockito.when(pedidoService.buscarPorId(1L)).thenReturn(pedidoResponseDTO);
        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void buscarPorFiltro() throws Exception {
        Mockito.when(pedidoService.buscarPorFiltro(any(), any(), any())).thenReturn(List.of(pedidoResponseDTO));
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void atualizarStatus() throws Exception {
        mockMvc.perform(patch("/api/pedidos/1/status?status=APROVADO"))
                .andExpect(status().isNoContent());
        Mockito.verify(pedidoService).atualizarStatus(1L, StatusPedido.APROVADO);
    }

    @Test
    void aprovarPedido() throws Exception {
        mockMvc.perform(post("/api/pedidos/1/aprovar"))
                .andExpect(status().isNoContent());
        Mockito.verify(pedidoService).aprovarPedido(1L);
    }

    @Test
    void cancelarPedido() throws Exception {
        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(pedidoService).cancelarPedido(1L);
    }
}
