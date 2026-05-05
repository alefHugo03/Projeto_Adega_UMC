CREATE DATABASE adega;

USE adega;

CREATE TABLE usuario (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    email VARCHAR(100),
    senha_hash VARCHAR(100)
);

INSERT INTO usuario (nome, email, senha_hash)
VALUES ('admin', 'admin@gmail.com', '123');

CREATE TABLE produto (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    tipo VARCHAR(50),
    valor_unit DECIMAL(10,2)
);

SELECT * FROM produto;

ALTER TABLE produto ADD quantidade INT DEFAULT 0;