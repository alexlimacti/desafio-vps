package com.vps.desafio.service;

import com.vps.desafio.dto.ParceiroRequestDTO;
import com.vps.desafio.dto.ParceiroResponseDTO;
import com.vps.desafio.model.Parceiro;
import com.vps.desafio.repository.ParceiroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParceiroService {
    private final ParceiroRepository parceiroRepository;

    public ParceiroService(ParceiroRepository parceiroRepository) {
        this.parceiroRepository = parceiroRepository;
    }

    @Transactional
    public ParceiroResponseDTO cadastrar(ParceiroRequestDTO dto) {
        Parceiro parceiro = Parceiro.builder()
                .nome(dto.nome())
                .limiteCredito(dto.limiteCredito())
                .creditoDisponivel(dto.limiteCredito())
                .build();
        parceiro = parceiroRepository.save(parceiro);
        return new ParceiroResponseDTO(
                parceiro.getId(),
                parceiro.getNome(),
                parceiro.getLimiteCredito(),
                parceiro.getCreditoDisponivel()
        );
    }
}

