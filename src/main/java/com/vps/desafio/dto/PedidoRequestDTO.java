package com.vps.desafio.dto;

import java.math.BigDecimal;
import java.util.List;

public record PedidoRequestDTO(
    Long parceiroId,
    List<ItemPedidoDTO> itens
) {}

