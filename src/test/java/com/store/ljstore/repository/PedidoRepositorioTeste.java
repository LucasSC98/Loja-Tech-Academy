package com.store.ljstore.repository;

import com.store.ljstore.model.Pedido;
import com.store.ljstore.model.Status;
import com.store.ljstore.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PedidoRepositorioTeste {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Test
    public void testSalvarPedido() {
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

        // Salva o pedido
        Pedido pedidoSalvo = pedidoRepositorio.save(pedido);

        // Verifica se o pedido foi salvo corretamente
        Optional<Pedido> pedidoEncontrado = pedidoRepositorio.findById(pedidoSalvo.getPedidoId());
        assertThat(pedidoEncontrado).isPresent();
        assertThat(pedidoEncontrado.get().getUsuario_id().getNomeCompleto()).isEqualTo("João Silva");
    }
}