package com.store.ljstore.dto;

import com.store.ljstore.model.Status;
import java.time.LocalDateTime;
import java.util.List;


public record PedidoRequestDTO(
        Integer usuario_id,
        String email,
        Double valorTotal,
        Status status,
        LocalDateTime dataPedido,
        List<ItemPedidoDTO> itens,
        String observacoes,
        String trackingCodigo
) {}

