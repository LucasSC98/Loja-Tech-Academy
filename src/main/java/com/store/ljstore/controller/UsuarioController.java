package com.store.ljstore.controller;

import com.store.ljstore.dto.UsuarioRequestDTO;
import com.store.ljstore.model.Genero;
import com.store.ljstore.model.Usuario;
import com.store.ljstore.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        return ResponseEntity.ok(usuarios);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioId(@PathVariable Integer id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado com o id: " + id));
        return ResponseEntity.ok(usuario);
    }

    // o get em /email/{email} retorna um usuário_id pelo email, usando para identificar o usuário que está fazendo o pedido
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> getUsuarioByEmail(@PathVariable String email) {
        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return ResponseEntity.ok(usuario);
    }
    @PostMapping("/criar-usuario")
    public ResponseEntity<Usuario> criarUsuario(
            @RequestBody UsuarioRequestDTO usuarioDTO) {
        if (usuarioDTO.nomeCompleto() == null || usuarioDTO.nomeCompleto().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nome do usuário não pode ser vazio");
        }
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(usuarioDTO.nomeCompleto());
        usuario.setEmail(usuarioDTO.email());
        usuario.setGenero(Genero.valueOf(usuarioDTO.genero().toLowerCase()));
        usuario.setEndereco(usuarioDTO.endereco());
        Usuario usuarioCriado = usuarioRepositorio.save(usuario);
        return ResponseEntity.ok(usuarioCriado);
    }
    @PutMapping("/atualizar-usuario/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(
            @PathVariable Integer id,
            @RequestBody UsuarioRequestDTO usuarioDTO) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado com o id: " + id));
        if (usuarioDTO.nomeCompleto() == null || usuarioDTO.nomeCompleto().trim().isEmpty()) { //trim() remove espaços em branco
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nome do usuário não pode ser vazio");
        }
        usuario.setNomeCompleto(usuarioDTO.nomeCompleto());
        usuario.setEmail(usuarioDTO.email());
        usuario.setGenero(Genero.valueOf(usuarioDTO.genero().toLowerCase()));
        usuario.setEndereco(usuarioDTO.endereco());
        Usuario usuarioAtualizado = usuarioRepositorio.save(usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }
    @DeleteMapping("/deletar-usuario/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Integer id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado com o id: " + id));
        usuarioRepositorio.delete(usuario);
        return ResponseEntity.noContent().build();
    }

}
