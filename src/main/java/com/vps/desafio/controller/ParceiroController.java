package com.vps.desafio.controller;

import com.vps.desafio.dto.ParceiroRequestDTO;
import com.vps.desafio.dto.ParceiroResponseDTO;
import com.vps.desafio.service.ParceiroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parceiros")
@Tag(name = "Parceiros", description = "API para cadastro de parceiros comerciais")
public class ParceiroController {
    private final ParceiroService parceiroService;

    public ParceiroController(ParceiroService parceiroService) {
        this.parceiroService = parceiroService;
    }

    @Operation(summary = "Cadastrar novo parceiro", description = "Cadastra um novo parceiro comercial com limite de crédito inicial.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parceiro cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ParceiroResponseDTO> cadastrar(@RequestBody ParceiroRequestDTO dto) {
        ParceiroResponseDTO response = parceiroService.cadastrar(dto);
        return ResponseEntity.ok(response);
    }
}

