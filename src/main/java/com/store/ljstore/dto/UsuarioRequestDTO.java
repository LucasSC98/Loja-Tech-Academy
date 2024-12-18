package com.store.ljstore.dto;

public record UsuarioRequestDTO(
        String nomeCompleto,
        String email,
        String genero,
        String endereco
) {}
