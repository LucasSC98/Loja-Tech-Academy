package com.store.ljstore.controller;

import com.store.ljstore.dto.PagamentoRequestDTO;
import com.store.ljstore.model.*;
import com.store.ljstore.repository.FormaPagamentoRepositorio;
import com.store.ljstore.repository.PagamentoRepositorio;
import com.store.ljstore.repository.PedidoRepositorio;
import com.store.ljstore.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    @Autowired
    private PagamentoRepositorio pagamentoRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private FormaPagamentoRepositorio formaPagamentoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping // vai retornar todos os pagamentos feitos //
    public ResponseEntity<List<Pagamento>> getAllPayments() {
        List<Pagamento> pagamentos = pagamentoRepositorio.findAll();
        return ResponseEntity.ok(pagamentos);
    }
    @GetMapping("/metodos-pagamento") // vai retornar todos os métodos de pagamento disponíveis para e vou usar com front //
    public ResponseEntity<List<FormaPagamento>> getMetodosPagamento() {
        List<FormaPagamento> metodosPagamento = formaPagamentoRepositorio.findAll();
        return ResponseEntity.ok(metodosPagamento);
    }
    // end point para criar um pagamento, recebendo o id do pedido e o id do métdo de pagamento, além do id do usuário //
    @PostMapping("/pagamento/{pedidoId}") //http://localhost:8080/pagamentos/pagamento/pedidoId
    public ResponseEntity<String> createPagamento(@PathVariable Integer pedidoId, @RequestBody PagamentoRequestDTO pagamentoRequestDTO) {
        if (pagamentoRequestDTO.metodoId() == null || pedidoId == null || pagamentoRequestDTO.usuarioId() == null) {
            return ResponseEntity.badRequest().body("metodo Id, pedido Id e usuario Id são obrigatórios.");
        } // verificando se os campos necessario para fazer pagamento foi preenchido //

        Optional<FormaPagamento> formaPagamento = formaPagamentoRepositorio.findById(pagamentoRequestDTO.metodoId());
        if (formaPagamento.isEmpty()) { // verificando se a forma de pagamento existe //
            return ResponseEntity.badRequest().body("metodo pagamento não encontrado.");
        }

        Optional<Pedido> pedido = pedidoRepositorio.findById(pedidoId);
        if (pedido.isEmpty()) { // verificando se o pedido existe //
            return ResponseEntity.badRequest().body("pedido não foi encontrado.");
        }

        Optional<Usuario> usuario = usuarioRepositorio.findById(pagamentoRequestDTO.usuarioId());
        if (usuario.isEmpty()) { // verificando se o usuário existe //
            return ResponseEntity.badRequest().body("usuario não encontrado.");
        }
        // pegando o valor total do pedido //
        Double valorTotal = pedido.get().getValorTotal();
        // criando o pagamento com os dados recebidos e salvando no banco de dados //
        Pagamento pagamento = new Pagamento();
        PagamentoPK pagamentoPK = new PagamentoPK();
        pagamentoPK.setPedidoId(pedidoId);
        pagamentoPK.setMetodoId(pagamentoRequestDTO.metodoId());

        pagamento.setId(pagamentoPK);
        pagamento.setPedido(pedido.get());
        pagamento.setFormaPagamento(formaPagamento.get());
        pagamento.setUsuario(usuario.get());
        pagamento.setValor(valorTotal);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setStatus(Pagamento.StatusPagamento.aprovado);
        pagamento.setIdentificadorTransacao(UUID.randomUUID().toString());

        Pedido pedidoAtualizado = pedido.get();
        pedidoAtualizado.setStatus(Status.enviado);
        // salvando o pagamento e atualizando o status do pedido //
        Pagamento salvarPagamento = pagamentoRepositorio.saveAndFlush(pagamento);

        return ResponseEntity.ok("Pagamento do pedido feito");
    }
    // end point para atualizar o metodo pagamento de um pedido //
    @PutMapping("/mudar-metodo-pagamento/{pedidoId}")
    public ResponseEntity<String> atualizarMetodoPagamento(@PathVariable Integer pedidoId, @RequestBody PagamentoRequestDTO pagamentoRequestDTO) {
        if (pagamentoRequestDTO.metodoId() == null || pagamentoRequestDTO.usuarioId() == null) {
            return ResponseEntity.badRequest().body("metodo Id e usuario Id são obrigatórios.");
        }

        Optional<FormaPagamento> formaPagamento = formaPagamentoRepositorio.findById(pagamentoRequestDTO.metodoId());
        if (formaPagamento.isEmpty()) {
            return ResponseEntity.badRequest().body("metodo pagamento não encontrado.");
        }

        Optional<Pedido> pedido = pedidoRepositorio.findById(pedidoId);
        if (pedido.isEmpty()) {
            return ResponseEntity.badRequest().body("pedido não foi encontrado.");
        }

        Optional<Usuario> usuario = usuarioRepositorio.findById(pagamentoRequestDTO.usuarioId());
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("usuario não encontrado.");
        }

        PagamentoPK pagamentoPK = new PagamentoPK();
        pagamentoPK.setPedidoId(pedidoId);
        pagamentoPK.setMetodoId(pagamentoRequestDTO.metodoId());

        Pagamento pagamento = new Pagamento();
        pagamento.setId(pagamentoPK);
        pagamento.setPedido(pedido.get());
        pagamento.setFormaPagamento(formaPagamento.get());
        pagamento.setUsuario(usuario.get());
        pagamento.setValor(pedido.get().getValorTotal());
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setStatus(Pagamento.StatusPagamento.aprovado);
        pagamento.setIdentificadorTransacao(UUID.randomUUID().toString());

        Pagamento salvarPagamento = pagamentoRepositorio.saveAndFlush(pagamento);

        return ResponseEntity.ok("Metodo de pagamento atualizado.");
    }
    // end point para deletar um pagamento, recebendo o id do pedido
    @DeleteMapping("/deletar-pagamento/{pedidoId}")
    public ResponseEntity<String> deletarPagamento(@PathVariable Integer pedidoId) {
        Optional<Pagamento> pagamento = pagamentoRepositorio.findByPedido_PedidoId(pedidoId);
        if (pagamento.isEmpty()) { // verificando se o pagamento existe //
            return ResponseEntity.badRequest().body("pagamento não foi encontrado.");
        }
        // deletando o pagamento //
        pagamentoRepositorio.delete(pagamento.get());

        return ResponseEntity.ok("Pagamento deletado");
    }


}
