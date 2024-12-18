package com.store.ljstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private Integer pedidoId;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario_id;

    @CreationTimestamp
    private LocalDateTime dataPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "valor_total")
    private Double valorTotal;

    @Column(name = "tracking_codigo")
    private String trackingCodigo;

    @Column(name = "observacoes")
    private String observacoes;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItensPedido> itens = new ArrayList<>();

    @PrePersist // prePersist é um evento que ocorre antes de salvar o objeto no banco de dados
    private void gerarTrackingCodigo() {
        if (this.trackingCodigo == null) {
            this.trackingCodigo = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
        }
    }

    public void addItem(ItensPedido item) { // adiciona um item ao pedido e seta o id do pedido no item adicionado
        if (itens == null) {
            itens = new ArrayList<>();
        }
        itens.add(item);
        item.setPedido(this);
        if (item.getId() == null) {
            item.setId(new ItensPedidoPK());
        }
        item.getId().setPedidoId(this.getPedidoId());
    }
}
