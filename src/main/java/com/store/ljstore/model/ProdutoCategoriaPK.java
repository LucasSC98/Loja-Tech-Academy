package com.store.ljstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class ProdutoCategoriaPK {
    @Column(name = "item_id")
    private Integer itemId;
    @Column(name = "categoria_id")
    private Integer categoriaId;
}
