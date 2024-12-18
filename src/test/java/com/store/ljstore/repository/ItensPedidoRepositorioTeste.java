package com.store.ljstore.repository;

import com.store.ljstore.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItensPedidoRepositorioTeste {

    @Autowired
    private ItemPedidoRepositorio itensPedidoRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Test
    public void testSalvarItensPedido() {
        // Cria um usuário
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("João Silva");
        usuario.setEmail("joao.silva@example.com");
        usuarioRepositorio.save(usuario);

        // Cria um pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario_id(usuario);
        pedido.setStatus(Status.pendente);
        pedido.setValorTotal(100.0);
        pedido.setObservacoes("Entrega rápida");
        pedidoRepositorio.save(pedido);

        // Cria um produto
        Produto produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setPrecoUnitario(50.0);
        produto.setQuantidade(10); // Define a quantidade do produto
        produtoRepositorio.save(produto);

        // Cria um item de pedido
        ItensPedidoPK id = new ItensPedidoPK();
        id.setPedidoId(pedido.getPedidoId());
        id.setItemId(produto.getId());

        ItensPedido itensPedido = new ItensPedido();
        itensPedido.setId(id);
        itensPedido.setPedido(pedido);
        itensPedido.setProduto(produto);
        itensPedido.setQuantidade(2);
        itensPedido.setPrecoUnitario(50.0);

        // Salva o item de pedido
        ItensPedido itensPedidoSalvo = itensPedidoRepositorio.save(itensPedido);

        // Verifica se o item de pedido foi salvo corretamente
        Optional<ItensPedido> itensPedidoEncontrado = itensPedidoRepositorio.findById(itensPedidoSalvo.getId());
        assertThat(itensPedidoEncontrado).isPresent();
        assertThat(itensPedidoEncontrado.get().getId().getItemId()).isEqualTo(produto.getId());
        assertThat(itensPedidoEncontrado.get().getId().getPedidoId()).isEqualTo(pedido.getPedidoId());
    }
}