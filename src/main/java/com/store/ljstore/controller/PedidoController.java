package com.store.ljstore.controller;

import com.store.ljstore.dto.ItemPedidoDTO;
import com.store.ljstore.dto.PedidoDetalhadoDTO;
import com.store.ljstore.dto.PedidoRequestDTO;
import com.store.ljstore.model.*;
import com.store.ljstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private ItemPedidoRepositorio ItemPedidoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PagamentoRepositorio pagamentoRepositorio;

    @GetMapping("/todos-pedidos")
    public List<Pedido> listarTodosPedidos() {
        return pedidoRepositorio.findAll();
    }

    @GetMapping("/{id}")
    public Pedido buscarPedidoPorId(Integer id) {
        return pedidoRepositorio.findById(id).orElse(null);
    }

    @PostMapping("/criar-pedido")
    public ResponseEntity<Pedido> criarPedido(@RequestBody PedidoRequestDTO pedidoDTO) {
        try {
            if (pedidoDTO.usuario_id() == null) { // precisa do id do usuário para fazer pedido, não pode ser nulo
                throw new IllegalArgumentException("precisa do id do usuário para fazer pedido");
            }

            Usuario usuario_id = usuarioRepositorio.findById(pedidoDTO.usuario_id()) // verifica se o usuário existe no banco
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não foi encontrado"));

            Pedido pedido = new Pedido(); // cria um novo pedido
            pedido.setUsuario_id(usuario_id); // seta o usuário_id no pedido
            pedido.setStatus(Status.pendente);
            pedido.setObservacoes(pedidoDTO.observacoes());
            pedido.setDataPedido(LocalDateTime.now()); // se a data do pedido não for informada, a data atual é setada
            pedido.setTrackingCodigo(pedidoDTO.trackingCodigo()); // seta o código de rastreio, gerado automaticamente na class Pedidos

            double valorTotal = 0.0; // inicializa o valor total do pedido


            if (pedidoDTO.itens() != null && !pedidoDTO.itens().isEmpty()) { // verifica se a lista de itens do pedido não é nula e não está vazia
                for (ItemPedidoDTO itemDTO : pedidoDTO.itens()) { // percorre a lista de itens do pedido
                    Produto produto = produtoRepositorio.findById(itemDTO.produtoId()) // procura o produto no banco pelo id para verificar se existe
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "o produto não encontrado: " + itemDTO.produtoId()));

                    if (produto.getQuantidade() < itemDTO.quantidade()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "não tem no estoque o produto: " + produto.getNome());
                    }

                    ItensPedido itemPedido = new ItensPedido();
                    ItensPedidoPK id = new ItensPedidoPK();
                    id.setItemId(produto.getId());
                    itemPedido.setId(id);
                    itemPedido.setProduto(produto);
                    itemPedido.setQuantidade(itemDTO.quantidade());
                    itemPedido.setPrecoUnitario(produto.getPrecoUnitario());

                    pedido.addItem(itemPedido); // adiciona o item ao pedido
                    valorTotal += produto.getPrecoUnitario() * itemDTO.quantidade(); // calcula o valor total do pedido baseado na quantidade de produtos
                    produto.setQuantidade(produto.getQuantidade() - itemDTO.quantidade()); // atualiza a quantidade do produto no estoque
                }
            }

            pedido.setValorTotal(valorTotal);
            Pedido pedidoSalvo = pedidoRepositorio.save(pedido); // salva o pedido no banco

            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoSalvo);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @GetMapping("/{pedidoId}/detalhes") //http://localhost:8080/pedidos/pedidoId/detalhes
    public ResponseEntity<PedidoDetalhadoDTO> obterDetalhesPedido(@PathVariable Integer pedidoId) {
        Pedido pedido = pedidoRepositorio.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O pedido não foi encontrado: "+pedidoId));

        List<ItemPedidoDTO> itens = pedido.getItens().stream().map(item -> new ItemPedidoDTO(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario()
        )).toList();

        Pagamento pagamento = pagamentoRepositorio.findByPedido_PedidoId(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O pagamento não foi encontrado"));

        String formaPagamento = pagamento.getFormaPagamento().getMetodoPagamento();

        PedidoDetalhadoDTO pedidoDetalhadoDTO = new PedidoDetalhadoDTO(
                pedido.getPedidoId(),
                pedido.getUsuario_id().getNomeCompleto(),
                pedido.getUsuario_id().getEndereco(),
                pedido.getDataPedido(),
                itens,
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getTrackingCodigo(),
                formaPagamento,
                pagamento.getStatus()

        );

        return ResponseEntity.ok(pedidoDetalhadoDTO);
    }
    @DeleteMapping("/deletar-pedido/{pedidoId}") // deleta o pedido por completo
    public ResponseEntity<String> deletarPedido(@PathVariable Integer pedidoId) {
        Pedido pedido = pedidoRepositorio.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O pedido não foi encontrado: "+pedidoId));
        Pagamento pagamento = pagamentoRepositorio.findByPedido_PedidoId(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O pagamento não foi encontrado: "+pedidoId));
        // busca os itens do pedido pelo id do pedido
        List<ItensPedido> itensPedido = ItemPedidoRepositorio.findByPedido_pedidoId(pedidoId);
        if (itensPedido.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Itens do pedido não encontrados");
        }
        // deleta os itens do pedido
        ItemPedidoRepositorio.deleteAll(itensPedido);
        // deleta o pagamento e o pedido
        pagamentoRepositorio.delete(pagamento);
        pedidoRepositorio.delete(pedido);

        return ResponseEntity.ok("Pedido deletado com sucesso");
    }
    //get que retorna os 10 ultimos pedidos conforme data

    @GetMapping("/ultimos-pedidos")
    public List<Pedido> listarUltimosPedidos() {
        return pedidoRepositorio.findTop10ByOrderByDataPedidoDesc();
    }

}
