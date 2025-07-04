package com.vps.desafio.repository;

import com.vps.desafio.model.Pedido;
import com.vps.desafio.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> findByStatusAndDataCriacaoBetween(StatusPedido status, LocalDateTime inicio, LocalDateTime fim);
}

