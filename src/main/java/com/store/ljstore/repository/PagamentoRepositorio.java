package com.store.ljstore.repository;

import com.store.ljstore.model.Pagamento;
import com.store.ljstore.model.PagamentoPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepositorio extends JpaRepository<Pagamento, PagamentoPK> {
    Optional<Pagamento> findByPedido_PedidoId(Integer pedidoId); // procura um pagamento pelo id do pedido
}
