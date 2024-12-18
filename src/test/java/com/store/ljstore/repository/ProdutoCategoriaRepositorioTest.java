package com.store.ljstore.repository;

import com.store.ljstore.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
public class ProdutoCategoriaRepositorioTest {
    @Autowired
    private ProdutoCategoriaRepositorio produtoCategoriaRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @Test
    public void testSaveAndFindProdutoCategoria() {
        // Cria e salva um Produto
        Produto produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setPrecoUnitario(10.99);
        produto.setQuantidade(1);
        produtoRepositorio.save(produto);

        // Cria e salva uma Categoria
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Teste");
        categoriaRepositorio.save(categoria);

        // Cria e salva uma ProdutoCategoria
        ProdutoCategoriaPK id = new ProdutoCategoriaPK();
        id.setItemId(produto.getId());
        id.setCategoriaId(categoria.getId());

        ProdutoCategoria produtoCategoria = new ProdutoCategoria();
        produtoCategoria.setId(id);
        produtoCategoria.setProduto(produto);
        produtoCategoria.setCategoria(categoria);

        produtoCategoriaRepositorio.save(produtoCategoria);

        // Verifica se a ProdutoCategoria foi salva corretamente
        ProdutoCategoria found = produtoCategoriaRepositorio.findById(id).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getProduto().getNome()).isEqualTo("Produto Teste");
        assertThat(found.getCategoria().getNome()).isEqualTo("Categoria Teste");
    }
}