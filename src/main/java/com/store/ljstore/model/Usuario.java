package com.store.ljstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Genero genero;

    @UpdateTimestamp
    private LocalDateTime dataCadastro;

    @Column(name = "endereco")
    private String endereco;

}
