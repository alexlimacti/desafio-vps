package com.vps.desafio.service;

import com.vps.desafio.dto.ItemPedidoDTO;
import com.vps.desafio.dto.PedidoRequestDTO;
import com.vps.desafio.dto.PedidoResponseDTO;
import com.vps.desafio.model.ItemPedido;
import com.vps.desafio.model.Pedido;
import com.vps.desafio.model.Parceiro;
import com.vps.desafio.model.StatusPedido;
import com.vps.desafio.repository.ItemPedidoRepository;
import com.vps.desafio.repository.PedidoRepository;
import com.vps.desafio.repository.ParceiroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ParceiroRepository parceiroRepository;
    private final NotificacaoService notificacaoService;
    private final ItemPedidoRepository itemPedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ParceiroRepository parceiroRepository, NotificacaoService notificacaoService, ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.parceiroRepository = parceiroRepository;
        this.notificacaoService = notificacaoService;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {
        Parceiro parceiro = parceiroRepository.findById(dto.parceiroId())
                .orElseThrow(() -> new EntityNotFoundException("Parceiro não encontrado"));
        BigDecimal valorTotal = dto.itens().stream()
                .map(i -> i.precoUnitario().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (parceiro.getCreditoDisponivel().compareTo(valorTotal) < 0) {
            throw new IllegalArgumentException("Crédito insuficiente");
        }
        Pedido pedido = Pedido.builder()
                .parceiro(parceiro)
                .valorTotal(valorTotal)
                .status(StatusPedido.PENDENTE)
                .build();
        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoDTO itemDTO : dto.itens()) {
            ItemPedido item = ItemPedido.builder()
                    .produto(itemDTO.produto())
                    .quantidade(itemDTO.quantidade())
                    .precoUnitario(itemDTO.precoUnitario())
                    .pedido(pedido)
                    .build();
            itens.add(item);
        }
        pedido.setItens(itens);
        pedido = pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(itens);
        notificacaoService.notificarMudancaStatus(pedido);
        return toResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        return toResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorFiltro(StatusPedido status, LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> pedidos;
        if (status != null && inicio != null && fim != null) {
            pedidos = pedidoRepository.findByStatusAndDataCriacaoBetween(status, inicio, fim);
        } else if (status != null) {
            pedidos = pedidoRepository.findByStatus(status);
        } else if (inicio != null && fim != null) {
            pedidos = pedidoRepository.findByDataCriacaoBetween(inicio, fim);
        } else {
            pedidos = pedidoRepository.findAll();
        }
        List<PedidoResponseDTO> dtos = new ArrayList<>();
        for (Pedido p : pedidos) {
            dtos.add(toResponseDTO(p));
        }
        return dtos;
    }

    @Transactional
    public void aprovarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Pedido não está pendente");
        }
        Parceiro parceiro = pedido.getParceiro();
        if (parceiro.getCreditoDisponivel().compareTo(pedido.getValorTotal()) < 0) {
            throw new IllegalArgumentException("Crédito insuficiente para aprovação");
        }
        parceiro.setCreditoDisponivel(parceiro.getCreditoDisponivel().subtract(pedido.getValorTotal()));
        pedido.setStatus(StatusPedido.APROVADO);
        pedidoRepository.save(pedido);
        parceiroRepository.save(parceiro);
        notificacaoService.notificarMudancaStatus(pedido);
    }

    @Transactional
    public void atualizarStatus(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
        notificacaoService.notificarMudancaStatus(pedido);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
        notificacaoService.notificarMudancaStatus(pedido);
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        List<ItemPedidoDTO> itens = new ArrayList<>();
        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                itens.add(new ItemPedidoDTO(item.getProduto(), item.getQuantidade(), item.getPrecoUnitario()));
            }
        }
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getParceiro().getId(),
                itens,
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getDataAtualizacao()
        );
    }
}
