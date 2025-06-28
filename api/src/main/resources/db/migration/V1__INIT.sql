-- Tabela Pessoa (API)
CREATE TABLE pessoa (
    id_pessoa SERIAL PRIMARY KEY,
    criacao_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    alteracao_registro TIMESTAMP,
    nome VARCHAR(120) NOT NULL,
    nascimento DATE,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    email VARCHAR(120),
    CONSTRAINT uq_pessoa_cpf UNIQUE (cpf)
);

-- Tabela Endereco (1‑to‑1 com Pessoa)
CREATE TABLE endereco (
    id_pessoa INTEGER PRIMARY KEY REFERENCES pessoa(id_pessoa) ON DELETE CASCADE,
    cep VARCHAR(8) NOT NULL,
    rua VARCHAR(120),
    numero VARCHAR(20),
    cidade VARCHAR(60),
    estado VARCHAR(60)
);

CREATE INDEX idx_pessoa_cpf ON pessoa(cpf);