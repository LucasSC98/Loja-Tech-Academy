package com.store.ljstore.dto;

import com.store.ljstore.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoDetalhadoDTO(
        Integer pedidoId,
        String nomeUsuario,
        String enderecoEntrega,
        LocalDateTime dataPedido,
        List<ItemPedidoDTO> itens,
        Double valorTotal,
        Status status,
        String trackingCodigo,
        String formaPagamento,
        com.store.ljstore.model.Pagamento.StatusPagamento statusPagamento
) {}