package com.vps.desafio.service;

import com.vps.desafio.model.Pedido;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {
    public void notificarMudancaStatus(Pedido pedido) {
        // Simulação de integração com mensageria (ex: log ou print)
        System.out.println("[NOTIFICAÇÃO] Pedido " + pedido.getId() + " mudou para status: " + pedido.getStatus());
    }
}

