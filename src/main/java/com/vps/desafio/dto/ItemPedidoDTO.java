package com.vps.desafio.dto;

import java.math.BigDecimal;

public record ItemPedidoDTO(
    String produto,
    Integer quantidade,
    BigDecimal precoUnitario
) {}
