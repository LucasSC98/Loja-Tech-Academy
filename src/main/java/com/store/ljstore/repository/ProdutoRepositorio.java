package com.store.ljstore.repository;

import com.store.ljstore.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ProdutoRepositorio extends JpaRepository <Produto, Integer> {

    List<Produto> findAllByOrderByPrecoUnitarioAsc(); //busca por preçc de produto em ordem crescente
    List<Produto> findAllByOrderByPrecoUnitarioDesc(); //busca por preço de produto em ordem decrescente
    List<Produto> findAllByOrderByNomeAsc(); //busca por nome de produto em ordem crescente
    List<Produto> findAllByNomeContaining(String nome); //busca por nome do produto
    List<Produto> findAllByOrderByNomeDesc(); //busca por nome de produto em ordem decrescente
    List<Produto> findByCategoriaId(Integer categoriaId); //busca por categoria de produto

}
