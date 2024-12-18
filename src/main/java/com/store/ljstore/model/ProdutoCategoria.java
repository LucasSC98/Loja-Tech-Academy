package com.store.ljstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "produtos_categorias")
@Getter
@Setter
public class ProdutoCategoria {

    @EmbeddedId
    private ProdutoCategoriaPK id;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Produto produto;

    @ManyToOne
    @MapsId("categoriaId")
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
