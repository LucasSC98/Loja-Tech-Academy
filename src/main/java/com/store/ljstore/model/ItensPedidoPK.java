package com.store.ljstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
public class ItensPedidoPK {
    @Column(name = "pedido_id")
    private Integer pedidoId;

    @Column(name = "item_id")
    private Integer itemId;

    public ItensPedidoPK() {}

    public ItensPedidoPK(Integer pedidoId, Integer itemId) {
        this.pedidoId = pedidoId;
        this.itemId = itemId;
    }

}