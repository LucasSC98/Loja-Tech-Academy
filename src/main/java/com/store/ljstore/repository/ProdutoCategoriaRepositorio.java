package com.store.ljstore.repository;

import com.store.ljstore.model.ProdutoCategoria;
import com.store.ljstore.model.ProdutoCategoriaPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoCategoriaRepositorio extends JpaRepository<ProdutoCategoria, ProdutoCategoriaPK> {
}
