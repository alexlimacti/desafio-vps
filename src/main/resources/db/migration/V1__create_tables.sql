-- V1__create_tables.sql
CREATE TABLE parceiro (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    limite_credito NUMERIC(19,2) NOT NULL,
    credito_disponivel NUMERIC(19,2) NOT NULL
);

CREATE TABLE pedido (
    id SERIAL PRIMARY KEY,
    parceiro_id INTEGER NOT NULL REFERENCES parceiro(id),
    valor_total NUMERIC(19,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE item_pedido (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
    produto VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(19,2) NOT NULL
);

