package com.store.ljstore.repository;

import com.store.ljstore.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {
    List<Pedido> findTop10ByOrderByDataPedidoDesc(); // vai retornar os 10 ultimos pedidos feitos
}