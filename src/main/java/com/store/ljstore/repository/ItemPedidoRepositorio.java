package com.store.ljstore.repository;

import com.store.ljstore.model.ItensPedido;
import com.store.ljstore.model.ItensPedidoPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepositorio extends JpaRepository<ItensPedido, ItensPedidoPK> {
    List<ItensPedido> findByPedido_pedidoId(Integer pedidoId); // vai retornar os itens de um pedido especifico buscando pelo id do pedido
}
