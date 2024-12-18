package com.store.ljstore.controller;

import com.store.ljstore.dto.CategoriaRequestDTO;
import com.store.ljstore.model.Categoria;
import com.store.ljstore.repository.CategoriaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {
    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @GetMapping("/todas-categorias") //http://localhost:8080/categoria/todas-categorias
    public ResponseEntity<List<Categoria>> getAllCategories() {
        List<Categoria> categorias = categoriaRepositorio.findAll();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaId(@PathVariable Long id) {
        Categoria categoria = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada com o id: " + id));
        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/atualizar-categoria/{id}") //http://localhost:8080/categoria/atualizar-categoria/id
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable Long id, @RequestBody CategoriaRequestDTO categoriadto) {
        Categoria categoria = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada com o id: " + id));
        if (categoriadto.nome() == null || categoriadto.nome().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nome da categoria não pode ser vazio");
        }
        categoria.setNome(categoriadto.nome());

        Categoria categoriaAtualizada = categoriaRepositorio.save(categoria);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/deletar-categoria/{id}") //http://localhost:8080/categoria/deletar-categoria/id
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        Categoria categoria = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada com o id: " + id));
        categoriaRepositorio.delete(categoria);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/criar-categoria") //http://localhost:8080/categoria/criar-categoria
    public ResponseEntity<Categoria> criarCategoria(@RequestBody CategoriaRequestDTO categoriadto) {
        if(categoriadto.nome() == null || categoriadto.nome().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nome da categoria não pode ser vazio");
        }
        Categoria categoria = new Categoria();
        categoria.setNome(categoriadto.nome());
        Categoria novaCategoria = categoriaRepositorio.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);

    }
    @DeleteMapping("/deletar-todas-categorias")
    public ResponseEntity<Void> deletarTodasCategorias() {
        categoriaRepositorio.deleteAll();
        return ResponseEntity.noContent().build();
    }

}
