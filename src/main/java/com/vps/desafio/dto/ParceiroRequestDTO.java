package com.vps.desafio.dto;

import java.math.BigDecimal;

public record ParceiroRequestDTO(
    String nome,
    BigDecimal limiteCredito
) {}

