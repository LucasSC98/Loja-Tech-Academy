package com.store.ljstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "auditoria_preco_produto")
@Getter
@Setter
public class AuditoriaPrecoProduto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auditoria_id")
    private Integer auditoriaId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "preco_antigo")
    private Double precoAntigo;

    @Column(name = "preco_novo")
    private Double precoNovo;

    @Column(name = "data_alteracao")
    private String dataAlteracao;
}
