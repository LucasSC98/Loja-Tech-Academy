# Megalojao


![Loja](https://i.imgur.com/ApcmZKO.png)

Este é um projeto de uma loja online desenvolvido como parte de um trabalho de faculdade. O projeto utiliza Java, Spring Boot, Maven e SQL para gerenciar usuários, produtos, pedidos, pagamentos e auditoria de preços.

## Tecnologias Utilizadas

- Java
- Javascript
- Spring Boot
- Maven
- SQL
- JPA/Hibernate
- Lombok

## Estrutura do Projeto

- `src/main/java/com/store/ljstore/model`: Contém as classes de modelo.
- `src/main/java/com/store/ljstore/repository`: Contém as interfaces de repositório.
- `src/main/java/com/store/ljstore/controller`: Contém os controladores REST.
- `src/main/resources/db/migration`: Contém os scripts de migração do banco de dados.

## Configuração do Banco de Dados

O projeto utiliza um banco de dados SQL. As tabelas são criadas e gerenciadas pelos scripts de migração localizados em `src/main/resources/db/migration`.

## Endpoints da API

### Usuários

- `GET /usuarios`: Retorna todos os usuários.
- `POST /usuarios`: Cria um novo usuário.

### Produtos

- `GET /produtos`: Retorna todos os produtos.
- `POST /produtos`: Cria um novo produto.

### Pedidos

- `POST /criar-pedido`: Cria um novo pedido.

### Pagamentos

- `GET /pagamentos`: Retorna todos os pagamentos.
- `POST /pagamentos/pagamento/{pedidoId}`: Cria um novo pagamento para um pedido.

### Auditoria de Preços

- `GET /auditoria/auditoria-preco-produto`: Retorna todos os registros de auditoria de preço de produto.

## Como Executar o Projeto

1. Clone o repositório:
    ```sh
    git clone https://github.com/LucasSC98/Loja-Tech-Academy.git
    ```
2. Navegue até o diretório do projeto:
    ```sh
    cd megalojao
    ```
3. Compile e execute o projeto usando Maven:
    ```sh
    mvn spring-boot:run


