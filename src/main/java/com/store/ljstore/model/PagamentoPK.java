package com.store.ljstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoPK {
    @Column(name = "pedido_id")
    private Integer pedidoId;

    @Column(name = "metodo_id")
    private Integer metodoId;
}
