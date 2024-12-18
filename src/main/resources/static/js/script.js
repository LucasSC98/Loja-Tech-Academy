let adminView = false;
let produtosAtuais = [];

// pagina de usuario
function usuarioView() {
    adminView = false;
    carregarProdutos();
    document.getElementById('adminToggleBtn').classList.remove('selected');
}

// pagina de admin
function mostarAdmView() {
    adminView = true;
    carregarProdutos();
    const adminToggleBtn = document.getElementById('adminToggleBtn');
    if (adminToggleBtn) {
        adminToggleBtn.classList.add('selected');
    } else {
        adminToggleBtn.classList.remove('selected');
    }
}

function limparCarrinho() {
    localStorage.removeItem('cartItems');
    document.getElementById('cartCount').textContent = 0;
}


// carrega todos os produtos
async function carregarProdutos() {
    try {
        const sortValue = document.getElementById('sortSelect').value;
        const [sortBy, direction] = sortValue.split(',');

        const response = await axios.get(`http://localhost:8080/produtos/todos?sortBy=${sortBy}&direction=${direction}`);
        produtosAtuais = response.data;
        mostrarProdutos();
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        alert('Erro ao carregar produtos');
    }
}
function formatarPreco(valor) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
}
// exibir produtos na tela
function mostrarProdutos() {
    const container = document.getElementById('productsContainer');
    container.innerHTML = '';

    if (adminView) { // botoes que so aparecem para no painel de admin
        const adicionarProdutoBtn = document.createElement('button');
        adicionarProdutoBtn.className = 'btn';
        adicionarProdutoBtn.textContent = 'Adicionar Produto';
        adicionarProdutoBtn.onclick = mostrarModaldeCriarProdutos;
        container.appendChild(adicionarProdutoBtn);

        const auditoriaButton = document.createElement('button');
        auditoriaButton.className = 'btn';
        auditoriaButton.textContent = 'Histórico de Preços';
        auditoriaButton.onclick = abrirModalAdt;
        const auditoriaId = document.getElementById('auditoriaButton');
        auditoriaId.innerHTML = '';
        auditoriaId.appendChild(auditoriaButton);

        const categoriaBtn = document.createElement('button');
        categoriaBtn.className = 'btn';
        categoriaBtn.textContent = 'Gerenciar Categorias';
        categoriaBtn.onclick = abrirCategoriaModal;
        auditoriaId.appendChild(categoriaBtn);

        const inputPedidoId = document.createElement('input');
        inputPedidoId.type = 'text';
        inputPedidoId.placeholder = 'Digite o ID do pedido';
        inputPedidoId.id = 'pedidoIdInput';
        auditoriaId.appendChild(inputPedidoId);

        const detalhesPedidoBtn = document.createElement('button');
        detalhesPedidoBtn.className = 'btn';
        detalhesPedidoBtn.textContent = 'Detalhes do Pedido';
        detalhesPedidoBtn.onclick = () => exibirDetalhesPedido(inputPedidoId.value);
        auditoriaId.appendChild(detalhesPedidoBtn);

    } else {
        const auditoriaId = document.getElementById('auditoriaButton');
        auditoriaId.innerHTML = '';
    }

    produtosAtuais.forEach(product => {
        if (!adminView && product.quantidade === 0) {
            return; // não exibe produtos sem estoque para usuário, somente no painel adm
        }

        const card = document.createElement('div');
        card.className = 'product-card';
        // card que exibe os produtos na tela com a imagem, nome, descricao, preco, categoria e quantidade, sempre que adicionar um novo produto, ele vai aparecer aqui
        card.innerHTML = `
            <img src="${product.imgUrl || 'https://cdn-icons-png.flaticon.com/512/1695/1695213.png'}" alt="${product.nome}" class="product-image">
            <div class="product-info">
                <h3>${product.nome}</h3>
                <p>${product.descricao}</p>
                <p class="product-price">${formatarPreco(product.precoUnitario)}</p>
                <p>Categoria(s): ${product.categoria.length > 0 ? product.categoria.map(c => c.nome).join(', ') : 'Sem categoria'}</p>
                ${adminView ? `<p>Quantidade: ${product.quantidade}</p>` : ''} 
            </div>
        `;

        // botoes que so aparecem para no painel de usuario como o botao de comprar
        if (!adminView) {
            card.innerHTML += `
                <button class="btn btn-add-cart" onclick="adicionarCarrinho(${product.id})">Comprar</button>
            `;
        } else {
            card.innerHTML += `
                <div class="admin-controls">
                    <button class="btn btn-edit" onclick="modalDeEdicao(${product.id})">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn btn-delete" onclick="deletarProdutos(${product.id})">
                        <i class="fas fa-trash"></i> Deletar
                    </button>
                </div>
            `;
        }

        container.appendChild(card);
    });
}


// funcao para abrir o modal de adicionar produto
function mostrarModaldeCriarProdutos() {
    document.getElementById('modalTitle').textContent = 'Adicionar Produto';
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('productModal').style.display = 'block';
    escolherCategorias();
}

// funcao para abrir o modal de editar produto
async function modalDeEdicao(id) {
    const product = produtosAtuais.find(p => p.id === id);
    if (!product) return;

    document.getElementById('modalTitle').textContent = 'Editar Produto';
    document.getElementById('nome').value = product.nome;
    document.getElementById('descricao').value = product.descricao;
    document.getElementById('precoUnitario').value = product.precoUnitario;
    document.getElementById('quantidade').value = product.quantidade;
    document.getElementById('imgUrl').value = product.imgUrl;
    document.getElementById('productId').value = product.id;

    const categoriasAtuaisDiv = document.getElementById('categoriasAtuais');
    categoriasAtuaisDiv.innerHTML = '';

    if (product.categoria && product.categoria.length > 0) {
        product.categoria.forEach(cat => {
            const categoriaTag = document.createElement('span');
            categoriaTag.className = 'categoria-tag';
            categoriaTag.innerHTML = `
                ${cat.nome}
                <button 
                    class="remove-categoria" 
                    onclick="removerCategoria(${product.id}, ${cat.id})"
                    title="Remover categoria"
                >
                    ×
                </button>
            `;
            categoriasAtuaisDiv.appendChild(categoriaTag);
        });
    } else {
        categoriasAtuaisDiv.innerHTML = '<em>Nenhuma categoria associada</em>';
    }

    document.getElementById('productModal').style.display = 'block';
    await escolherCategorias();
}
// funcao para remover categoria de cada produto
async function removerCategoria(productId, categoriaId) {
    try {
        const response = await axios.delete(`/produtos/${productId}/delete-categoria/${categoriaId}`);
        if (response.status === 200) {
            await carregarProdutos();
            modalDeEdicao(productId);
            showToast('Categoria removida com sucesso!');
        }
    } catch (error) {
        console.error('Erro ao remover categoria:', error);
        showToast('Erro ao remover categoria. Tente novamente.', true);
    }
}


function fecharModalProduto() {
    document.getElementById('productModal').style.display = 'none';
}

// gereciamento de produtos, adicionar, editar, atualizar e deletar
async function gerenciadorProdutos(event) {
    event.preventDefault();

    const productData = {
        nome: document.getElementById('nome').value,
        descricao: document.getElementById('descricao').value,
        precoUnitario: parseFloat(document.getElementById('precoUnitario').value),
        quantidade: parseInt(document.getElementById('quantidade').value),
        imgUrl: document.getElementById('imgUrl').value
    };

    const productId = document.getElementById('productId').value;
    const categoriaIds = Array.from(document.getElementById('categoriaId').selectedOptions).map(option => option.value);

    try {
        if (productId) { // se o produto ja existir, atualiza
            await axios.put(`http://localhost:8080/produtos/atualizar-produto/${productId}`, productData);
            for (const categoriaId of categoriaIds) {
                await axios.put(`http://localhost:8080/produtos/${productId}/add-categoria/${categoriaId}`);
            }
        } else { // se nao existir, cria um novo
            const response = await axios.post(`http://localhost:8080/produtos/criar-produto`, productData);
            for (const categoriaId of categoriaIds) {
                await axios.put(`http://localhost:8080/produtos/${response.data.id}/add-categoria/${categoriaId}`);
            }
        }

        fecharModalProduto();
        carregarProdutos();
    } catch (error) {
        console.error('Erro ao salvar produto:', error);
        alert('Erro ao salvar produto');
    }
}

async function deletarProdutos(id) {
    if (!confirm('Tem certeza que deseja deletar este produto?')) return;

    try {
        await axios.delete(`http://localhost:8080/produtos/deletar-produto/${id}`);
        carregarProdutos();
    } catch (error) {
        console.error('Erro ao deletar produto:', error);
        alert('Erro ao deletar produto');
    }
}

async function procurarProdutos(query) {
    try {
        if (query.trim()) {
            const response = await axios.get(`http://localhost:8080/produtos/search?name=${query}`);
            produtosAtuais = response.data;
        } else {
            await carregarProdutos();
        }
        mostrarProdutos();
    } catch (error) {
        console.error('Erro na busca:', error);
        alert('Erro ao buscar produtos');
    }
}
function carregarCategorias() {
    axios.get('http://localhost:8080/categoria/todas-categorias')
        .then(response => {
            const container = document.getElementById('categoriasContainer');
            container.innerHTML = ''; // Limpa o container antes de preencher

            response.data.forEach(categoria => {
                // Criação do elemento de cada categoria
                const categoriaItem = document.createElement('div');
                categoriaItem.classList.add('categoria-item');
                categoriaItem.dataset.id = categoria.id;

                categoriaItem.innerHTML = `
                    <span>${categoria.nome}</span>
                    <button class="btn btn-edit" onclick="editarCategoria(${categoria.id}, '${categoria.nome}')">Editar</button>
                `;

                container.appendChild(categoriaItem);
            });
        })
        .catch(error => {
            alert('Erro ao carregar categorias: ' + error.message);
        });
}

function criarOuAtualizarCategoria() {
    const nome = document.getElementById('nomeCategoria').value;
    const categoriaId = document.getElementById('categoriasalvar').value; // Alterado aqui

    if (!nome) {
        notificarUsuario('Erro', 'Nome da categoria é obrigatório!');
        return;
    }

    if (categoriaId) {
        axios.put(`http://localhost:8080/categoria/atualizar-categoria/${categoriaId}`, { nome })
            .then(() => {
                notificarUsuario('Sucesso', 'Categoria atualizada com sucesso!');
                carregarCategorias();
                fecharGerenciadorDeCategorias();
            })
            .catch(error => {
                alert('Erro ao atualizar categoria: ' + error.message);
            });
    } else {
        axios.post('http://localhost:8080/categoria/criar-categoria', { nome })
            .then(() => {
                alert('Categoria criada com sucesso!');
                carregarCategorias();
                fecharGerenciadorDeCategorias();
            })
            .catch(error => {
                alert('Erro ao criar categoria: ' + error.message);
            });
    }
}

function editarCategoria(id, nome) {
    document.getElementById('categoriasalvar').value = id; // Alterado aqui
    document.getElementById('nomeCategoria').value = nome;
    document.getElementById('gerenciarCategoriasModal').style.display = 'flex';
}
// não deleta as categorias que estão sendo usadas, por ser uma fk

// function deletarCategoria(id) {
//     if (confirm('Tem certeza que deseja deletar esta categoria?')) {
//         axios.delete(`http://localhost:8080/categoria/deletar-categoria/${id}`)
//             .then(() => {
//                 alert('Categoria deletada com sucesso!');
//                 carregarCategorias();
//             })
//             .catch(error => {
//                 alert('Erro ao deletar categoria: ' + error.message);
//             });
//     }
// }

function fecharGerenciadorDeCategorias() {
    // Fecha o modal e limpa os campos do formulário
    document.getElementById('gerenciarCategoriasModal').style.display = 'none';
    document.getElementById('gerenciarCategoriasForm').reset();
    document.getElementById('categoriaId').value = ''; // Limpa o ID
}



// function para criar usuario ou login com nome, email, genero e endereco

function mostrarModalCadastro() {
    document.getElementById('authModal').style.display = 'block';
}

function fecharModalCadastro() {
    document.getElementById('authModal').style.display = 'none';
}

document.getElementById('authForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const userData = {
        nomeCompleto: document.getElementById('nomeCompleto').value,
        email: document.getElementById('email').value,
        genero: document.getElementById('genero').value,
        endereco: document.getElementById('endereco').value
    };

    try {
        await axios.post('http://localhost:8080/usuarios/criar-usuario', userData);
        alert('Usuário cadastrado com sucesso!');
        fecharModalCadastro();
    } catch (error) {
        alert('Erro ao cadastrar usuário: ' + error.message);
    }
});

//  vai fechar o modal quando clicar fora dele
window.onclick = function (event) {
    if (event.target === document.getElementById('authModal')) {
        fecharModalCadastro();
    }
}
//<button class="cart-btn" onclick="abrirCarrinho()">
//     <i class="fas fa-shopping-cart"></i>
//     <span id="cartCount">0</span>
// </button>// *
function abrirCarrinho() {
    document.getElementById('cartModal').style.display = 'block';
    mostrarItensCarrinho();
}

function mostrarItensCarrinho() {
    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
    const container = document.getElementById('cartTableBody');
    container.innerHTML = '';

    let total = 0;

    cartItems.forEach(product => {
        const itemTotal = product.precoUnitario * product.quantidade;
        total += itemTotal;

        const row = document.createElement('tr');
        row.innerHTML = `
            <td><img src="${product.imgUrl}" alt="${product.nome}" style="width: 50px; height: 50px"></td>
            <td>${product.nome}</td>
            <td>R$ ${product.precoUnitario.toFixed(2)}</td>
            <td>${product.quantidade}</td>
            <td>R$ ${itemTotal.toFixed(2)}</td>
            <td>
                <button class="btn btn-remove" onclick="removerProduto(${product.id})">
                    <i class="fas fa-trash"></i>
                </button>
                </td>
        `;
        container.appendChild(row);
    });

    document.getElementById('cartTotal').textContent = total.toFixed(2);
    document.getElementById('cartCount').textContent = cartItems.length;
}

function adicionarCarrinho(productId) {
    const product = produtosAtuais.find(p => p.id === productId);
    if (!product) return;

    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
    const itemexistente = cartItems.find(item => item.id === productId);

    if (itemexistente) {

        if (itemexistente.quantidade < product.quantidade) {
            itemexistente.quantidade++;
        } else {
            notificarUsuario('Erro', 'Quantidade insuficiente no estoque!');
            return;
        }
    } else {
        if (product.quantidade > 0) {
            cartItems.push({...product, quantidade: 1});
        } else {
            notificarUsuario('Erro', 'Este produto está fora de estoque!');
            return;
        }
    }

    localStorage.setItem('cartItems', JSON.stringify(cartItems));
    document.getElementById('cartCount').textContent = cartItems.length;
    notificarUsuario('Sucesso', 'Produto adicionado ao carrinho!');
}

function removerProduto(productId) {
    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
    const updatedCartItems = cartItems.filter(item => item.id !== productId);
    localStorage.setItem('cartItems', JSON.stringify(updatedCartItems));
    document.getElementById('cartCount').textContent = updatedCartItems.length;
    notificarUsuario('Erro', 'Produto removido do carrinho!');
    mostrarItensCarrinho();
}

function fecharCarrinho() {
    document.getElementById('cartModal').style.display = 'none';
}

async function confirmarPedido() {
    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
    // verifica se o carrinho esta vazio
    if (cartItems.length === 0) {
        notificarUsuario('Erro', 'O carrinho está vazio.');
        return;
    }
    // pegar o email do input do usuario no modal de usuario e verificar se o usuario esta logado
    const email = document.getElementById('emailInput').value;
    if (!email) {
        abrirModalUsuario();
        return;
    }

    try { // pegar o id do usuario no modal
        const userId = document.getElementById('usuarioModal').dataset.userId;
        if (!userId) {
            throw new Error('Usuário não encontrado ou não logado.');
        }
        // cria o pedido
        const pedidoDTO = {
            usuario_id: userId,
            usuarioEmail: email,
            observacoes: '',
            itens: cartItems.map(item => ({
                produtoId: item.id,
                quantidade: item.quantidade
            }))
        };
        // envia o pedido com um post
        const response = await axios.post(`http://localhost:8080/pedidos/criar-pedido`, pedidoDTO);
        if (response.status === 201) {
            const pedidoId = response.data.pedidoId; // pega o id do pedido
            notificarUsuario('Sucesso', 'Pedido criado com sucesso!');

            localStorage.removeItem('cartItems'); // limpa o carrinho
            document.getElementById('cartCount').textContent = '0';

            abrirModalPagamento(pedidoId, userId);  // abre o modal de pagamento e passa o id do pedido e do usuario
            return { pedidoId, userId }; // retorna o id do pedido e do usuario
        } else {
            throw new Error('Erro ao criar pedido');
        }
    } catch (error) {
        notificarUsuario('Erro', 'Erro ao criar pedido, precisa do id de usuario: ' + error.message);
        abrirModalUsuario();
        return null;
    }
}

function notificarUsuario(type, message) {
    const toast = document.getElementById(type === 'Erro' ? 'pedidoErro' : 'pedidoSucesso');
    toast.querySelector('p').textContent = message;
    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
    }, 5000);
}

async function carregarMetodosPagamento() {
    try {
        const response = await axios.get('http://localhost:8080/pagamentos/metodos-pagamento');
        const metodosPagamento = response.data;
        const select = document.getElementById('metodoPagamento');
        select.innerHTML = '';

        metodosPagamento.forEach(metodo => {
            const option = document.createElement('option');
            option.value = metodo.metodoId;
            option.textContent = metodo.metodoPagamento;
            select.appendChild(option);
        });
    } catch (error) {
        alert('Erro ao carregar métodos de pagamento', error);
    }
}

function abrirModalPagamento(pedidoId, userId) {
    const modal = document.getElementById('modalPagamento');
    modal.style.display = 'block';
    modal.dataset.pedidoId = pedidoId;
    modal.dataset.userId = userId;
    carregarMetodosPagamento();
}

async function processarPagamento(event) {
    event.preventDefault();

    const modal = document.getElementById('modalPagamento');
    const pedidoId = modal.dataset.pedidoId;
    const usuarioId = modal.dataset.userId;
    const metodoPagamentoId = document.getElementById('metodoPagamento').value;

    if (!pedidoId || !metodoPagamentoId || !usuarioId) {
        // alert('Dados incompletos para processar o pagamento.');
        return;
    }

    try {
        const response = await axios.post(`http://localhost:8080/pagamentos/pagamento/${pedidoId}`, {
            metodoId: metodoPagamentoId,
            usuarioId: usuarioId
        });

        if (response.status === 200) {
            notificarUsuario('Sucesso', 'Pagamento processado com sucesso!');
            modal.style.display = 'none';
            exibirDetalhesPedido(pedidoId);
        } else {
            throw new Error('Erro ao processar pagamento');
        }
    } catch (error) {
        alert('Erro ao processar pagamento: ' + error.message);
    }
}

function fecharModalPagamento() {
    document.getElementById('modalPagamento').style.display = 'none';
}

async function exibirDetalhesPedido(pedidoId) {
    try {
        const response = await fetch(`/pedidos/${pedidoId}/detalhes`);
        if (!response.ok) {
            throw new Error('Falha ao buscar detalhes do pedido');
        }
        const pedido = await response.json();

        document.getElementById('usuarioNome').value = pedido.nomeUsuario || 'Nome não disponível';
        document.getElementById('usuarioEndereco').value = pedido.enderecoEntrega || 'Endereço não disponível';
        document.getElementById('dataPedido').value = pedido.dataPedido ? new Date(pedido.dataPedido).toLocaleString() : 'Data não disponível';
        document.getElementById('valorTotal').value = pedido.valorTotal ? `R$ ${pedido.valorTotal.toFixed(2)}` : 'Valor não disponível';
        document.getElementById('status').value = pedido.status || 'Status não disponível';
        document.getElementById('trackingCodigo').value = pedido.trackingCodigo || 'Código não disponível';
        document.getElementById('formaPagamento').value = pedido.formaPagamento || 'Forma de pagamento não disponível';
        document.getElementById('pagamentoStatus').value = pedido.statusPagamento || 'Status de pagamento não disponível';


        const itensTableBody = document.getElementById('itensTableBody');
        itensTableBody.innerHTML = '';
        if (pedido.itens && pedido.itens.length > 0) {
            pedido.itens.forEach(item => {
                const row = itensTableBody.insertRow();
                row.insertCell(0).textContent = item.nomeProduto || 'Nome não disponível';
                row.insertCell(1).textContent = item.quantidade || 'Quantidade não disponível';
                row.insertCell(2).textContent = item.precoUnitario ? `R$ ${item.precoUnitario.toFixed(2)}` : 'Preço não disponível';
            });
        } else {
            const row = itensTableBody.insertRow();
            row.insertCell(0).textContent = 'Nenhum item encontrado';
            row.cells[0].colSpan = 3;
        }
        document.getElementById('detalhesPedidoModal').style.display = 'block';
    } catch (error) {
        console.error('Erro:', error);
        alert('Não foi possível carregar os detalhes do pedido. Por favor, tente novamente.');
    }
}

function fecharDetalhesPedidoModal() {
    document.getElementById('detalhesPedidoModal').style.display = 'none';
    limparCarrinho();
}

async function abrirModalAdt() {
    const modal = document.getElementById('auditoriaModal');
    if (modal) {
        modal.style.display = 'block';
        await carregarAuditoria();
    }
    carregarAuditoria();
}

function closeAuditoriaModal() {
    document.getElementById('auditoriaModal').style.display = 'none';
}

async function carregarAuditoria() {
    try {
        const container = document.getElementById('auditoriaTableBody');
        if (!container) {
            console.error('Elemento auditoriaTableBody não encontrado');
            return;
        }

        const response = await axios.get('http://localhost:8080/auditoria/auditoria-preco-produto');
        const auditoria = response.data;

        container.innerHTML = '';

        auditoria.forEach(item => {
            const row = container.insertRow();
            row.innerHTML = `
                <td>${item.auditoriaId}</td>
                <td>${item.itemId}</td>
                <td>R$ ${item.precoAntigo.toFixed(2)}</td>
                <td>R$ ${item.precoNovo.toFixed(2)}</td>
                <td>${new Date(item.dataAlteracao).toLocaleString()}</td>
            `;
        });
    } catch (error) {
        alert('Erro ao carregar o historico de preços', error);
    }
}

//function para carregar categorias
//http://localhost:8080/categoria/todas-categorias
function escolherCategorias() {
    axios.get('http://localhost:8080/categoria/todas-categorias')
        .then(response => {
            const lista = document.getElementById('listaCategorias');
            const select = document.getElementById('categoriaId');
            if (!lista || !select) {
                return;
            }
            lista.innerHTML = '';
            select.innerHTML = '';

            response.data.forEach(categoria => {
                // vai criar um li para cada categoria carregada do banco
                const li = document.createElement('li');
                li.textContent = categoria.nome;
                li.style.cursor = 'pointer';
                li.onclick = () => carregarProdutosPorCategoria(categoria.id);
                lista.appendChild(li);

                const option = document.createElement('option');
                option.value = categoria.id;
                option.textContent = categoria.nome;
                select.appendChild(option);
            });
        })
        .catch(() => {
            alert('Erro ao carregar todas as categorias');
        });
}

//function para carregar produtos por categoria
// vai pegar o id da categoria e carregar os produtos daquela categoria, filtrando pelo id
async function carregarProdutosPorCategoria(categoriaId) {
    try {
        const response = await axios.get(`http://localhost:8080/produtos/categoria/${categoriaId}`);
        produtosAtuais = response.data;
        mostrarProdutos();
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        alert('Erro ao carregar produtos');
    }
}

function abrirCategoriaModal() {
    document.getElementById('gerenciarCategoriasModal').style.display = 'block';
    carregarCategorias();
}

function toggleLista() {
    escolherCategorias();
    const lista = document.getElementById('listaCategorias');
    lista.style.display = lista.style.display === 'block' ? 'none' : 'block';
}

// event listener para fechar a lista de categorias ao clicar fora dela
document.addEventListener('click', function (event) {
    const lista = document.getElementById('listaCategorias');
    const target = event.target;
    if (!lista.contains(target) && !target.matches('[onclick*="toggleLista"]')) {
        lista.style.display = 'none';
    }
});

function abrirModalUsuario() {
    document.getElementById('usuarioModal').style.display = 'block';
}

function closeUserModal() {
    document.getElementById('usuarioModal').style.display = 'none';
}

function usuarioDetalhes() {
    const email = document.getElementById('emailInput').value;
    if (!email) {
        notificarUsuario('Erro', 'Email é necessário para buscar o usuário.');
        return;
    }

    axios.get(`http://localhost:8080/usuarios/email/${email}`)
        .then(response => {
            const usuario = response.data;
            document.getElementById('mostrarNome').value = usuario.nomeCompleto;
            document.getElementById('mostrarEmail').value = usuario.email;
            document.getElementById('mostrarGenero').value = usuario.genero;
            document.getElementById('mostrarEndereco').value = usuario.endereco;
            document.getElementById('usuarioModal').dataset.userId = usuario.usuarioId; // salva o id do usuario no modal
        })
        .catch(error => {
            notificarUsuario('Erro', 'Erro ao buscar usuário: ' + error.message);
            abrirModalUsuario();
        });
}
//function para atualizar o usuario junto de usuariodetalhes
function atualizarUsuario() {
    const userId = document.getElementById('usuarioModal').dataset.userId; // pega o id do usuario
    const usuarioData = {
        nomeCompleto: document.getElementById('mostrarNome').value,
        email: document.getElementById('mostrarEmail').value,
        genero: document.getElementById('mostrarGenero').value,
        endereco: document.getElementById('mostrarEndereco').value
    };

    axios.put(`http://localhost:8080/usuarios/atualizar-usuario/${userId}`, usuarioData)
        .then(() => {
            notificarUsuario('Sucesso', 'Usuário atualizado com sucesso!');
        })
        .catch(error => {
            notificarUsuario('Erro', 'Erro ao atualizar usuário: ' + error.message);
        });
}

//@GetMapping("/{pedidoId}/detalhes") digitar o id do pedido e pegar os detalhes do pedido com o exibirDetalhesPedido



document.getElementById('productForm').addEventListener('submit', gerenciadorProdutos);
document.getElementById('searchInput').addEventListener('input', (e) => procurarProdutos(e.target.value));
document.getElementById('sortSelect').addEventListener('change', carregarProdutos);

window.onload = function () {
    limparCarrinho();
    carregarProdutos();
};
document.addEventListener('DOMContentLoaded', function () {
    const formPagamento = document.getElementById('formPagamento');
    if (formPagamento) {
        formPagamento.addEventListener('submit', processarPagamento);
    }
});