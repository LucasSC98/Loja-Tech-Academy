package com.store.ljstore.dto;

import java.util.Set;

public record ProdutoRequestDTO(
        String nome,
        Double precoUnitario,
        String imgUrl,
        Integer quantidade,
        String descricao,
        Set<Integer> categoriasId
) {}