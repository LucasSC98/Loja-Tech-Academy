create table usuarios
(
    usuario_id    int primary key auto_increment,
    nome_completo varchar(80)         not null,
    email         varchar(255) unique not null,
    genero        enum('masculino', 'feminino', 'outro'),
    data_cadastro timestamp default current_timestamp,
    endereco      varchar(225)
);

create table produtos
(
    item_id          int primary key auto_increment,
    nome             varchar(255)   not null,
    descricao        text,
    preco_unitario   decimal(10, 2) not null,
    quantidade       int            not null,
    desconto         decimal(10, 2) default 0,
    img_url          varchar(255),
    data_criacao     timestamp      default current_timestamp,
    data_atualizacao timestamp      default current_timestamp
);

create table categorias
(
    categoria_id     int primary key auto_increment,
    nome             varchar(255) not null,
    data_criacao     timestamp default current_timestamp,
    data_atualizacao timestamp default current_timestamp
);

create table produtos_categorias
(
    item_id      int not null,
    categoria_id int not null,
    primary key (item_id, categoria_id),
    foreign key (item_id) references produtos (item_id),
    foreign key (categoria_id) references categorias (categoria_id)
);

create table pedidos
(
    pedido_id       int primary key auto_increment,
    usuario_id      int            not null,
    data_pedido     timestamp default current_timestamp,
    status          enum('pendente', 'aprovado', 'entregue', 'cancelado') default 'pendente',
    valor_total     decimal(10, 2) not null,
    tracking_codigo varchar(100),
    data_entrega    date,
    observacoes     text,
    foreign key (usuario_id) references usuarios (usuario_id)
);

create table itens_pedido
(
    pedido_id      int            not null,
    item_id        int            not null,
    quantidade     int            not null,
    preco_unitario decimal(10, 2) not null,
    primary key (pedido_id, item_id),
    foreign key (pedido_id) references pedidos (pedido_id),
    foreign key (item_id) references produtos (item_id)
);

create table formas_pagamento
(
    metodo_id int primary key auto_increment,
    metodo    enum('cartão de crédito', 'pix', 'boleto', 'paypal')
);

create table pagamentos
(
    pedido_id               int not null,
    metodo_id               int not null,
    data_pagamento          timestamp default current_timestamp,
    valor                   decimal(10, 2),
    status                  enum('pendente', 'aprovado', 'rejeitado', 'cancelado') default 'pendente',
    identificador_transacao varchar(225),
    data_autorizacao        date,
    primary key (pedido_id, metodo_id),
    foreign key (pedido_id) references pedidos (pedido_id),
    foreign key (metodo_id) references formas_pagamento (metodo_id)
);

create table auditoria_preco_produto
(
    auditoria_id   int primary key auto_increment,
    item_id        int not null,
    preco_antigo   decimal(10, 2),
    preco_novo     decimal(10, 2),
    data_alteracao timestamp default current_timestamp,
    foreign key (item_id) references produtos (item_id)
);