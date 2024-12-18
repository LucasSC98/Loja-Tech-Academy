package com.store.ljstore.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "formas_pagamento")
@EqualsAndHashCode
public class FormaPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metodo_id")
    private Integer metodoId;

    @Column(name= "metodo_pagamento")
    private String metodoPagamento;

}
