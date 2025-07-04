# Desafio Java - Sistema de Gestão de Pedidos B2B

## Descrição
Microserviço para gestão de pedidos B2B, desenvolvido em Java 17 com Spring Boot, PostgreSQL, Swagger/OpenAPI e containerização Docker.

## Pré-requisitos
- Docker e Docker Compose instalados
- Java 17+ e Maven (apenas para builds locais)

## Como executar

### 1. Build do projeto
Se desejar buildar localmente, execute:
```sh
./mvnw clean package
```

### 2. Subir a aplicação e o banco de dados
Execute o comando abaixo na raiz do projeto:
```sh
docker-compose up --build
```

- A API estará disponível em: http://localhost:8080
- O Swagger estará disponível em: http://localhost:8080/swagger-ui.html ou http://localhost:8080/swagger-ui/
- O banco PostgreSQL estará disponível em: localhost:5432 (usuário: postgres, senha: postgres)

### 3. Parar os containers
```sh
docker-compose down
```

## Endpoints principais
- Cadastro de parceiros: `POST /api/parceiros`
- Cadastro de pedidos: `POST /api/pedidos`
- Consulta de pedidos: `GET /api/pedidos`, `GET /api/pedidos/{id}`
- Atualização de status: `PATCH /api/pedidos/{id}/status`
- Aprovação de pedido: `POST /api/pedidos/{id}/aprovar`
- Cancelamento: `DELETE /api/pedidos/{id}`

## Observações
- O sistema simula notificações de status via log.
- O controle de crédito do parceiro é automático na criação e aprovação do pedido.
- O projeto está preparado para alta concorrência e escalabilidade.

## Testes automatizados
Para rodar os testes:
```sh
./mvnw test
```

---

Dúvidas ou sugestões? Abra uma issue!
