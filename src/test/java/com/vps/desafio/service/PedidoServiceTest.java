package com.vps.desafio.service;

import com.vps.desafio.dto.ItemPedidoDTO;
import com.vps.desafio.dto.PedidoRequestDTO;
import com.vps.desafio.model.Parceiro;
import com.vps.desafio.model.Pedido;
import com.vps.desafio.model.StatusPedido;
import com.vps.desafio.repository.ItemPedidoRepository;
import com.vps.desafio.repository.PedidoRepository;
import com.vps.desafio.repository.ParceiroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceTest {
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ParceiroRepository parceiroRepository;
    @Mock
    private NotificacaoService notificacaoService;
    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pedidoService = new PedidoService(pedidoRepository, parceiroRepository, notificacaoService, itemPedidoRepository);
    }

    @Test
    void criarPedido_sucesso() {
        Parceiro parceiro = Parceiro.builder()
                .id(1L)
                .creditoDisponivel(new BigDecimal("1000.00"))
                .build();
        when(parceiroRepository.findById(1L)).thenReturn(Optional.of(parceiro));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        PedidoRequestDTO dto = new PedidoRequestDTO(1L, List.of(
                new ItemPedidoDTO("Produto A", 2, new BigDecimal("100.00")),
                new ItemPedidoDTO("Produto B", 1, new BigDecimal("200.00"))
        ));

        var response = pedidoService.criarPedido(dto);
        assertNotNull(response);
        assertEquals(StatusPedido.PENDENTE, response.status());
        assertEquals(new BigDecimal("400.00"), response.valorTotal());
        verify(notificacaoService, times(1)).notificarMudancaStatus(any(Pedido.class));
    }

    @Test
    void criarPedido_creditoInsuficiente() {
        Parceiro parceiro = Parceiro.builder()
                .id(1L)
                .creditoDisponivel(new BigDecimal("100.00"))
                .build();
        when(parceiroRepository.findById(1L)).thenReturn(Optional.of(parceiro));

        PedidoRequestDTO dto = new PedidoRequestDTO(1L, List.of(
                new ItemPedidoDTO("Produto A", 2, new BigDecimal("100.00"))
        ));
        assertThrows(IllegalArgumentException.class, () -> pedidoService.criarPedido(dto));
    }

    @Test
    void aprovarPedido_sucesso() {
        Parceiro parceiro = Parceiro.builder()
                .id(1L)
                .creditoDisponivel(new BigDecimal("500.00"))
                .build();
        Pedido pedido = Pedido.builder()
                .id(10L)
                .status(StatusPedido.PENDENTE)
                .valorTotal(new BigDecimal("200.00"))
                .parceiro(parceiro)
                .build();
        when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(parceiroRepository.save(any(Parceiro.class))).thenReturn(parceiro);

        pedidoService.aprovarPedido(10L);
        assertEquals(StatusPedido.APROVADO, pedido.getStatus());
        assertEquals(new BigDecimal("300.00"), parceiro.getCreditoDisponivel());
        verify(notificacaoService, times(1)).notificarMudancaStatus(pedido);
    }

    @Test
    void aprovarPedido_creditoInsuficiente() {
        Parceiro parceiro = Parceiro.builder()
                .id(1L)
                .creditoDisponivel(new BigDecimal("100.00"))
                .build();
        Pedido pedido = Pedido.builder()
                .id(10L)
                .status(StatusPedido.PENDENTE)
                .valorTotal(new BigDecimal("200.00"))
                .parceiro(parceiro)
                .build();
        when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));
        assertThrows(IllegalArgumentException.class, () -> pedidoService.aprovarPedido(10L));
    }

    @Test
    void cancelarPedido_sucesso() {
        Pedido pedido = Pedido.builder()
                .id(20L)
                .status(StatusPedido.PENDENTE)
                .build();
        when(pedidoRepository.findById(20L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        pedidoService.cancelarPedido(20L);
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
        verify(notificacaoService, times(1)).notificarMudancaStatus(pedido);
    }
}
