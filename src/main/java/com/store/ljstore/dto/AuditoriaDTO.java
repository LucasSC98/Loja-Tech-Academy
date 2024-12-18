package com.store.ljstore.dto;

public record AuditoriaDTO(
        Integer id,
        Integer produtoId,
        String nomeProduto,
        Double precoAntigo,
        Double precoNovo,
        String dataModificacao
) {
}
