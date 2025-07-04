package com.vps.desafio.service;

import com.vps.desafio.dto.ParceiroRequestDTO;
import com.vps.desafio.dto.ParceiroResponseDTO;
import com.vps.desafio.model.Parceiro;
import com.vps.desafio.repository.ParceiroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ParceiroServiceTest {
    @Mock
    private ParceiroRepository parceiroRepository;

    @InjectMocks
    private ParceiroService parceiroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        parceiroService = new ParceiroService(parceiroRepository);
    }

    @Test
    void cadastrarParceiro_sucesso() {
        Parceiro parceiro = Parceiro.builder()
                .id(1L)
                .nome("Empresa XPTO")
                .limiteCredito(new BigDecimal("1000.00"))
                .creditoDisponivel(new BigDecimal("1000.00"))
                .build();
        when(parceiroRepository.save(any(Parceiro.class))).thenReturn(parceiro);

        ParceiroRequestDTO request = new ParceiroRequestDTO("Empresa XPTO", new BigDecimal("1000.00"));
        ParceiroResponseDTO response = parceiroService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Empresa XPTO", response.nome());
        assertEquals(new BigDecimal("1000.00"), response.limiteCredito());
        assertEquals(new BigDecimal("1000.00"), response.creditoDisponivel());
    }
}

