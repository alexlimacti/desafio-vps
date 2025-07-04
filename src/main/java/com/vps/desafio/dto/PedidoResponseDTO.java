package com.vps.desafio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.vps.desafio.model.StatusPedido;

public record PedidoResponseDTO(
    Long id,
    Long parceiroId,
    List<ItemPedidoDTO> itens,
    BigDecimal valorTotal,
    StatusPedido status,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao
) {}

