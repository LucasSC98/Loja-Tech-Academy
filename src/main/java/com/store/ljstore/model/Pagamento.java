package com.store.ljstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
public class Pagamento {
    @EmbeddedId
    private PagamentoPK id;

    @ManyToOne
    @MapsId("pedidoId")
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @MapsId("metodoId")
    @JoinColumn(name = "metodo_id")
    private FormaPagamento formaPagamento;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "valor")
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusPagamento status;

    @Column(name = "identificador_transacao")
    private String identificadorTransacao = UUID.randomUUID().toString();

    @Column(name = "data_autorizacao")
    private LocalDate dataAutorizacao;

    public enum StatusPagamento {
        pendente, aprovado, cancelado
        //enum('pendente','aprovado','cancelado')
    }

    @PrePersist
    private void gerarIdentificadorTransacao() {
        if (this.identificadorTransacao == null) {
            this.identificadorTransacao = String.valueOf(ThreadLocalRandom.current().nextLong(1_00_00_00L, 10_00_00_00L));
        }
    }

}
