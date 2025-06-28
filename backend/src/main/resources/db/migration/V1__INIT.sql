CREATE TABLE pessoa (
    id_pessoa SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    nascimento DATE,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    email VARCHAR(120),
    situacao_integracao VARCHAR(20) NOT NULL DEFAULT 'NAO_ENVIADO',
    CONSTRAINT uq_pessoa_cpf UNIQUE (cpf)
);

CREATE TABLE endereco (
    id_pessoa INTEGER PRIMARY KEY REFERENCES pessoa(id_pessoa) ON DELETE CASCADE,
    cep VARCHAR(8) NOT NULL,
    rua VARCHAR(120),
    numero INTEGER,
    cidade VARCHAR(60),
    estado VARCHAR(60)
);

CREATE INDEX idx_pessoa_cpf ON pessoa(cpf);