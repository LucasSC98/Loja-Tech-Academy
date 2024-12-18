package com.store.ljstore.repository;

import com.store.ljstore.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email); // fazer a busca pelo id do usuario pelo email
//    Optional<Usuario> findIdByEmail(String email);
}
