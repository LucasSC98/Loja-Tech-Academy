package com.store.ljstore.dto;

public record ItemPedidoDTO (
        Integer produtoId,
        String nomeProduto,
        Integer quantidade,
        Double precoUnitario
    ) {}
