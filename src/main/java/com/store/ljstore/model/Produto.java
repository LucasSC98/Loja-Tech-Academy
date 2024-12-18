package com.store.ljstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"dataCriacao", "dataAtualizacao"})
public class Produto {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;

    @Column(name = "nome") // nome da coluna no banco
    private String nome;

    @Column(name = "preco_unitario", nullable = false)
    @NotNull(message = "Preço unitário é obrigatório")// nome da coluna no banco
    private Double precoUnitario;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "desconto")
    private Double desconto;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "img_url") // nome da coluna no banco
    private String imgUrl;

    @CreationTimestamp
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    @ManyToMany
    @JoinTable(
            name = "produtos_categorias",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categoria;

}