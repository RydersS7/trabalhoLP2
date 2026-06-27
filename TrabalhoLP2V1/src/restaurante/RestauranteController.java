package restaurante;

import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RestauranteController - Gerencia toda a lógica de negócio do restaurante
 * Separa a lógica da interface gráfica (MainFrame)
 * ATUALIZADO: Relacionamento Cliente ↔ Mesa implementado
 */
public class RestauranteController {

    // ---- DADOS DO SISTEMA ----
    private List<Mesa> listaMesas;
    private List<Cliente> listaClientes;
    private List<ItemCardapio> listaCardapio;
    private List<Pedido> listaPedidosAtivos;

    /**
     * Construtor - Inicializa o sistema
     */
    public RestauranteController() {
        this.listaMesas = new ArrayList<>();
        this.listaClientes = new ArrayList<>();
        this.listaCardapio = new ArrayList<>();
        this.listaPedidosAtivos = new ArrayList<>();
        
        inicializarMesas();
    }

    /**
     * Cria as 10 mesas iniciais do restaurante
     */
    private void inicializarMesas() {
        for (int i = 1; i <= 10; i++) {
            Mesa mesa = new Mesa();
            mesa.setNumero(i);
            mesa.setStatus(StatusMesa.LIVRE);
            listaMesas.add(mesa);
        }
    }

    // ======================== GETTERS (Para MainFrame acessar dados) ========================

    public List<Mesa> getListaMesas() {
        return listaMesas;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public List<ItemCardapio> getListaCardapio() {
        return listaCardapio;
    }

    public List<Pedido> getListaPedidosAtivos() {
        return listaPedidosAtivos;
    }

    // ======================== LÓGICA DE MESAS ========================

    /**
     * Abre uma mesa (muda status para OCUPADA e cria um novo pedido)
     * ATUALIZADO: Agora recebe o cliente que vai sentar
     */
    public void abrirMesa(int numeroMesa, Cliente cliente) throws IllegalStateException {
        if (cliente == null) {
            throw new IllegalStateException("Cliente não pode ser nulo");
        }

        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa == null) {
            throw new IllegalStateException("Mesa " + numeroMesa + " não encontrada");
        }
        
        if (!mesa.getStatus().equals(StatusMesa.LIVRE)) {
            throw new IllegalStateException("Mesa " + numeroMesa + " já está ocupada");
        }

        // Cria novo pedido para a mesa
        Pedido novoPedido = new Pedido();
        novoPedido.setMesa(mesa);
        novoPedido.setCliente(cliente);
        novoPedido.setStatus("ABERTO");
        
        // Atualiza a mesa COM o cliente
        mesa.setClienteAtual(cliente);
        mesa.ocuparMesa(novoPedido);
        
        listaPedidosAtivos.add(novoPedido);
        cliente.adicionarPedidoAoHistorico(novoPedido);
    }

    /**
     * Abre uma mesa sem cliente especificado (cliente será adicionado depois)
     * Útil se o cliente não quer se cadastrar
     */
    public void abrirMesaSemCliente(int numeroMesa) throws IllegalStateException {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa == null) {
            throw new IllegalStateException("Mesa " + numeroMesa + " não encontrada");
        }
        
        if (!mesa.getStatus().equals(StatusMesa.LIVRE)) {
            throw new IllegalStateException("Mesa " + numeroMesa + " já está ocupada");
        }

        // Cria novo pedido para a mesa SEM cliente
        Pedido novoPedido = new Pedido();
        novoPedido.setMesa(mesa);
        novoPedido.setStatus("ABERTO");
        mesa.setClienteAtual(null);
        mesa.ocuparMesa(novoPedido);
        listaPedidosAtivos.add(novoPedido);
    }

    /**
     * Fecha uma mesa (libera e remove o pedido)
     */
    public void fecharMesa(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa != null && mesa.getPedidoAtual() != null) {
            listaPedidosAtivos.remove(mesa.getPedidoAtual());
            mesa.liberarMesa();  // Limpa pedido E cliente
        }
    }

    /**
     * Busca uma mesa pelo número
     */
    private Mesa buscarMesaPorNumero(int numero) {
        for (Mesa mesa : listaMesas) {
            if (mesa.getNumero() == numero) {
                return mesa;
            }
        }
        return null;
    }

    /**
     * Obtém o cliente que está em uma mesa
     */
    public Cliente obterClienteDaMesa(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa != null) {
            return mesa.getClienteAtual();
        }
        
        return null;
    }

    /**
     * Associa um cliente a uma mesa já aberta (útil se abriu sem cliente)
     */
    public void associarClienteAMesa(int numeroMesa, Cliente cliente) throws IllegalStateException {
        if (cliente == null) {
            throw new IllegalStateException("Cliente não pode ser nulo");
        }

        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa == null) {
            throw new IllegalStateException("Mesa não encontrada");
        }
        
        if (mesa.getPedidoAtual() == null) {
            throw new IllegalStateException("Mesa não tem pedido aberto");
        }

        // Associa cliente à mesa E ao pedido
        mesa.setClienteAtual(cliente);
        mesa.getPedidoAtual().setCliente(cliente);
    }

    // ======================== LÓGICA DE CARDÁPIO ========================

    /**
     * Adiciona uma comida ao cardápio
     */
    public void adicionarComida(String nome, double preco, String descricao) {
        Comida novaComida = new Comida();
        novaComida.setNome(nome);
        novaComida.setPreco(preco);
        novaComida.setDescricao(descricao);
        novaComida.setIngredientes(new ArrayList<>());
        
        listaCardapio.add(novaComida);
    }

    /**
     * Adiciona uma bebida ao cardápio
     */
    public void adicionarBebida(String nome, double preco, String fornecedor, String volume) {
        Bebida novaBebida = new Bebida();
        novaBebida.setNome(nome);
        novaBebida.setPreco(preco);
        novaBebida.setFornecedor(fornecedor);
        novaBebida.setVolume(volume);
        
        listaCardapio.add(novaBebida);
    }

    /**
     * Remove um item do cardápio
     */
    public void removerItemCardapio(ItemCardapio item) {
        listaCardapio.remove(item);
    }

    // ======================== LÓGICA DE CLIENTES ========================

    /**
     * Cadastra um novo cliente
     */
    public void cadastrarCliente(String nome, String cpf, String email) {
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setCpf(cpf);
        novoCliente.setEmail(email);
        novoCliente.setBonus(0.0);
        
        listaClientes.add(novoCliente);
    }

    /**
     * Remove um cliente
     */
    public void removerCliente(Cliente cliente) {
        listaClientes.remove(cliente);
    }

    /**
     * Busca um cliente pelo CPF
     */
    public Cliente buscarClientePorCpf(String cpf) {
        for (Cliente cliente : listaClientes) {
            if (cliente.getCpf().equals(cpf)) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Busca um cliente pelo nome (busca parcial)
     */
    public Cliente buscarClientePorNome(String nome) {
        for (Cliente cliente : listaClientes) {
            if (cliente.getNome().equalsIgnoreCase(nome)) {
                return cliente;
            }
        }
        return null;
    }

    // ======================== LÓGICA DE PEDIDOS ========================

    /**
     * Adiciona um item ao pedido de uma mesa
     */
    public void adicionarItemAoPedido(int numeroMesa, ItemCardapio item, int quantidade) 
            throws IllegalStateException {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa == null) {
            throw new IllegalStateException("Mesa não encontrada");
        }
        
        Pedido pedido = mesa.getPedidoAtual();
        if (pedido == null) {
            throw new IllegalStateException("Mesa não tem pedido aberto");
        }

        ItemPedido novoItemPedido = new ItemPedido(item, quantidade);
        pedido.adicionarItem(novoItemPedido);
    }

    /**
     * Remove um item do pedido de uma mesa
     */
    public void removerItemDoPedido(int numeroMesa, ItemCardapio item) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa != null && mesa.getPedidoAtual() != null) {
            Pedido pedido = mesa.getPedidoAtual();
            pedido.getItens().removeIf(ip -> ip.getItem().equals(item));
        }
    }

    /**
     * Calcula o total de um pedido
     */
    public double calcularTotalPedido(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa == null || mesa.getPedidoAtual() == null) {
            return 0.0;
        }
        
        return mesa.getPedidoAtual().calcularTotal();
    }

    /**
     * Efetua o pagamento de um pedido
     */
    public void efetuarPagamento(int numeroMesa, String tipoPagamento, boolean usarBonus) 
            throws IllegalStateException {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa == null || mesa.getPedidoAtual() == null) {
            throw new IllegalStateException("Mesa não tem pedido aberto");
        }

        Pedido pedido = mesa.getPedidoAtual();
        Cliente cliente = mesa.getClienteAtual();
        
        double totalOriginal = pedido.calcularTotal();
        double totalAPagar = totalOriginal;

        // Se há cliente associado, registra pagamento com bônus
        if (cliente != null) {
            pedido.setCliente(cliente);
            
            if (usarBonus) {
                double bonus = cliente.getBonus();
                if (bonus >= totalAPagar) {
                    totalAPagar = 0;
                    cliente.usarBonus(bonus);
                } else {
                    totalAPagar -= bonus;
                    cliente.usarBonus(bonus);
                }
            }

            // Adiciona bônus equivalente a 10% do total pago
            cliente.adicionarBonus(totalAPagar);
            
            System.out.println("Mesa: " + mesa.getNumero());
            System.out.println("Cliente: " + cliente.getNome());
            System.out.println("Pagamento realizado com " + tipoPagamento);
            System.out.println("Total original: R$ " + String.format("%.2f", totalOriginal));
            System.out.println("Total pago: R$ " + String.format("%.2f", totalAPagar));
        } else {
            // Paga sem cliente (sem bônus)
            System.out.println("Mesa: " + mesa.getNumero());
            System.out.println("Pagamento realizado com " + tipoPagamento);
            System.out.println("Total pago: R$ " + String.format("%.2f", totalAPagar));
        }
        
        pedido.setStatus("PAGO");
        pedido.setDataHoraFechada(java.time.LocalDateTime.now());
        
        // Remove dos ativos e fecha a mesa
        listaPedidosAtivos.remove(pedido);
        fecharMesa(numeroMesa);
    }

    /**
     * Obtém o pedido ativo de uma mesa
     */
    public Pedido buscarPedidoDaMesa(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa != null) {
            return mesa.getPedidoAtual();
        }
        
        return null;
    }

    // ======================== MÉTODOS AUXILIARES ========================

    /**
     * Retorna a quantidade de mesas ocupadas
     */
    public int contarMesasOcupadas() {
        int count = 0;
        for (Mesa mesa : listaMesas) {
            if (!mesa.getStatus().equals(StatusMesa.LIVRE)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retorna a quantidade de mesas livres
     */
    public int contarMesasLivres() {
        return listaMesas.size() - contarMesasOcupadas();
    }

    /**
     * Valida se um item pode ser adicionado ao cardápio
     */
    public boolean validarItemCardapio(String nome, double preco) {
        return nome != null && !nome.trim().isEmpty() && preco > 0;
    }

    /**
     * Retorna informação sobre uma mesa (útil para debug)
     */
    public String obterInfoMesa(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        
        if (mesa == null) {
            return "Mesa não encontrada";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Mesa ").append(mesa.getNumero()).append(" - ");
        info.append("Status: ").append(mesa.getStatus()).append(" - ");
        
        if (mesa.getClienteAtual() != null) {
            info.append("Cliente: ").append(mesa.getClienteAtual().getNome());
        } else {
            info.append("Cliente: Nenhum");
        }
        
        return info.toString();
    }

    /**
     * Calcula o faturamento total do dia (pedidos pagos)
     */
    public double calcularFaturamentoTotal() {
        double total = 0;
        for (Cliente cliente : listaClientes) {
            total += cliente.getTotalGasto();
        }
        return total;
    }

    /**
     * Calcula o tempo médio de permanência nas mesas
     */
    public double calcularTempoMedioPermanencia() {
        if (listaClientes.isEmpty()) return 0;

        long totalMinutos = 0;
        int pedidosFinalizados = 0;

        for (Cliente cliente : listaClientes) {
            for (Pedido pedido : cliente.getHistoricoPedidos()) {
                if (pedido.getStatus().equals("PAGO")) {
                    totalMinutos += pedido.obterMinutosDePermanencia();
                    pedidosFinalizados++;
                }
            }
        }

        if (pedidosFinalizados == 0) return 0;
        return (double) totalMinutos / pedidosFinalizados;
    }

    /**
     * Retorna tempo formatado para exibição no dashboard
     */
    public String obterTempoMedioFormatado() {
        double minutos = calcularTempoMedioPermanencia();
        long horas = (long) minutos / 60;
        long mins = (long) minutos % 60;
        if (horas > 0) {
            return horas + "h " + mins + "m";
        }
        return mins + "m";
    }

    /**
     * Obtém cliente por nome (busca exata) ou retorna null
     */
    public Cliente obterClientePorNomeExato(String nome) {
        return buscarClientePorNome(nome);
    }
}