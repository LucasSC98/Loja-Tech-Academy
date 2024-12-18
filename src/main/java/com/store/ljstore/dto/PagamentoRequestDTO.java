package com.store.ljstore.dto;



import java.time.LocalDateTime;


public record PagamentoRequestDTO(
        Integer pedidoId,
        Integer metodoId,
        Integer usuarioId,
        LocalDateTime dataPagamento
){}
