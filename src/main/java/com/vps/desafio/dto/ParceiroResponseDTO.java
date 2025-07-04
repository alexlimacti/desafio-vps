package com.vps.desafio.dto;

import java.math.BigDecimal;

public record ParceiroResponseDTO(
    Long id,
    String nome,
    BigDecimal limiteCredito,
    BigDecimal creditoDisponivel
) {}

