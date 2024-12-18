package com.store.ljstore.dto;

public record PedidoDetalhes(
        Integer id,
        String nome,
        Double precoUnitario,
        Integer quantidade,
        Double precoTotal,
        String status,
        String dataCriacao,
        String dataEntrega,
        UsuarioRequestDTO usuario,
        ProdutoRequestDTO produto
) {
}
