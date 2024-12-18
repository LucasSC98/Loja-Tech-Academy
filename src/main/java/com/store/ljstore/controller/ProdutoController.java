package com.store.ljstore.controller;

import com.store.ljstore.dto.ProdutoRequestDTO;
import com.store.ljstore.model.Categoria;
import com.store.ljstore.model.Produto;
import com.store.ljstore.repository.CategoriaRepositorio;
import com.store.ljstore.repository.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;


@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @GetMapping("/todos") //http://localhost:8080/produtos/todos
    public ResponseEntity<List<Produto>> listarTodosProdutos(
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        List<Produto> produtos = switch (sortBy.toLowerCase()) {
            case "price" -> direction.equalsIgnoreCase("desc") ?
                    produtoRepositorio.findAllByOrderByPrecoUnitarioDesc() :
                    produtoRepositorio.findAllByOrderByPrecoUnitarioAsc();
            case "nome" -> direction.equalsIgnoreCase("desc") ?
                    produtoRepositorio.findAllByOrderByNomeDesc() :
                    produtoRepositorio.findAllByOrderByNomeAsc();
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Parâmetro de Ordenação inválido");
        };

        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}") //http://localhost:8080/produtos/id
    public ResponseEntity<Produto> getProdutoId(@PathVariable int id) {
        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "produto com o id: " + id + " não encontrado"));
        return ResponseEntity.ok(produto);
    }

    @PostMapping("/criar-produto") //http://localhost:8080/produtos/criar-produto
    public ResponseEntity<Produto> criarProduto(@RequestBody ProdutoRequestDTO productDTO) {
        if (productDTO.nome() == null || productDTO.nome().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "o nome do produto não pode ser vazio");
        }

        Produto produto = new Produto();
        produto.setNome(productDTO.nome());
        produto.setPrecoUnitario(productDTO.precoUnitario());
        produto.setQuantidade(productDTO.quantidade());
        produto.setImgUrl(productDTO.imgUrl());
        produto.setDescricao(productDTO.descricao());


        Produto salvarNovoProduto= produtoRepositorio.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvarNovoProduto);
    }
    // end point para atualizar um produto (nome, preço, quantidade, descrição, imgUrl)
    @PutMapping("/atualizar-produto/{id}") //http://localhost:8080/produtos/atualizar-produto/id
    public ResponseEntity<Produto> updateProduto(
            @PathVariable int id,
            @RequestBody ProdutoRequestDTO productDTO) {
        // verifica se o produto existe
        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "produto com o id: " + id + " não encontrado"));
        // verificacoes para atualizar o produto
        if (productDTO.nome() != null) {
            produto.setNome(productDTO.nome()); }
        if (productDTO.precoUnitario() != null) {
            produto.setPrecoUnitario(productDTO.precoUnitario()); }
        if (productDTO.quantidade() != null) {
            produto.setQuantidade(productDTO.quantidade()); }
        if (productDTO.descricao() != null) {
            produto.setDescricao(productDTO.descricao()); }
        if (productDTO.imgUrl() != null) {
            produto.setImgUrl(productDTO.imgUrl()); }

        // salva o produto atualizado
        Produto updatedProduto = produtoRepositorio.save(produto);
        return ResponseEntity.ok(updatedProduto);
    }

    @DeleteMapping("/deletar-produto/{id}") //http://localhost:8080/produtos/deletar-produto/id
    public ResponseEntity<Void> deleteProduto(@PathVariable int id) {
        Produto produto = produtoRepositorio.findById(id) // verifica se o produto existe
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "produto com o id: " + id + " não encontrado"));
        // se existir deleta o produto
        produtoRepositorio.delete(produto);
        return ResponseEntity.noContent().build();
    }
    // end point para buscar produtos com base no nome
    @GetMapping("/search")//http://localhost:8080/produtos/search?name=produto
    public ResponseEntity<List<Produto>> searchProducts(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "o campo de busca não pode ser vazio");
        }
        // busca os produtos pelo nome
        List<Produto> produtos = produtoRepositorio.findAllByNomeContaining(name);
        return ResponseEntity.ok(produtos);
    }
    // end point para adicionar uma categoria a um produto
    @PutMapping("{id}/add-categoria/{categoriaId}") //http://localhost:8080/produtos/id/add-categoria/categoriaId
    public ResponseEntity<Produto> addCategoria(@PathVariable Integer id, @PathVariable Long categoriaId) {
        Produto produto = produtoRepositorio.findById(id) // verifica se o produto existe
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "produto com o id: " + id + " não encontrado"));
        Categoria categoria = categoriaRepositorio.findById(categoriaId) // verifica se a categoria existe
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "categoria com o id: " + categoriaId + " não encontrado"));
        // se existir adiciona a categoria ao produto
        produto.getCategoria().add(categoria);

        Produto updatedProduto = produtoRepositorio.save(produto);
        return ResponseEntity.ok(updatedProduto);
    }

    // end point para buscar produtos por categoria, filtra todos os produtos da categoria
    @GetMapping("/categoria/{categoriaId}") //http://localhost:8080/produtos/categoria/categoriaId
    public List<Produto> getProdutosByCategoria(@PathVariable Integer categoriaId) {
    return produtoRepositorio.findByCategoriaId(categoriaId);
}
    //deletar categoria do produto
    @DeleteMapping("{id}/delete-categoria/{categoriaId}") //http://localhost:8080/produtos/id/delete-categoria/categoriaId
    public ResponseEntity<Produto> deleteCategoria(@PathVariable Integer id, @PathVariable Long categoriaId) {
        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "produto com o id: " + id + " não encontrado"));
        Categoria categoria = categoriaRepositorio.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "categoria com o id: " + categoriaId + " não encontrado"));
        produto.getCategoria().remove(categoria);
        Produto updatedProduto = produtoRepositorio.save(produto);
        return ResponseEntity.ok(updatedProduto);
    }
}