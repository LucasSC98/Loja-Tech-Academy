package com.store.ljstore.repository;

import com.store.ljstore.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PagamentoRepositorioTest {

    @Autowired
    private PagamentoRepositorio pagamentoRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private FormaPagamentoRepositorio formaPagamentoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Test
    public void testSalvarPagamento() {
        // Cria um usuário
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Lucas");
        usuario.setEmail("lucas@example.com");
        usuarioRepositorio.save(usuario);

        // Cria um pedido
        Pedido pedido = new Pedido();
        pedido.setStatus(Status.pendente);
        pedido.setValorTotal(200.0);
        pedido.setUsuario_id(usuario);
        pedidoRepositorio.save(pedido);

        // Cria uma forma de pagamento
        FormaPagamento formaPagamento = new FormaPagamento();
//        formaPagamento.setMetodo(FormaPagamento.MetodoPagamento.pix);
        formaPagamentoRepositorio.save(formaPagamento);

        // Cria um pagamento
        PagamentoPK id = new PagamentoPK();
        id.setPedidoId(pedido.getPedidoId());
        id.setMetodoId(formaPagamento.getMetodoId());

        Pagamento pagamento = new Pagamento();
        pagamento.setId(id);
        pagamento.setPedido(pedido);
        pagamento.setFormaPagamento(formaPagamento);
        pagamento.setUsuario(usuario);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setValor(200.0);
        pagamento.setStatus(Pagamento.StatusPagamento.aprovado);
        pagamento.setIdentificadorTransacao("123456");
        pagamento.setDataAutorizacao(LocalDate.now());

        // Salva o pagamento
        Pagamento pagamentoSalvo = pagamentoRepositorio.save(pagamento);

        // Verifica se o pagamento foi salvo corretamente
        Optional<Pagamento> pagamentoEncontrado = pagamentoRepositorio.findById(pagamentoSalvo.getId());
        assertThat(pagamentoEncontrado).isPresent();
        assertThat(pagamentoEncontrado.get().getId().getPedidoId()).isEqualTo(pedido.getPedidoId());
        assertThat(pagamentoEncontrado.get().getId().getMetodoId()).isEqualTo(formaPagamento.getMetodoId());
    }
}