package restaurante;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class RestauranteController {

    private List<Mesa> listaMesas;
    private List<Cliente> listaClientes;
    private List<ItemCardapio> listaCardapio;
    private List<Pedido> listaPedidosAtivos;
    private List<Pedido> listaPedidosFinalizados;

    public RestauranteController() {
        this.listaMesas = new ArrayList<>();
        this.listaClientes = new ArrayList<>();
        this.listaCardapio = new ArrayList<>();
        this.listaPedidosAtivos = new ArrayList<>();
        this.listaPedidosFinalizados = new ArrayList<>();

        inicializarMesas();
        inicializarCardapio();
    }

    private void inicializarMesas() {
        for (int i = 1; i <= 10; i++) {
            Mesa mesa = new Mesa(i, StatusMesa.LIVRE);
            listaMesas.add(mesa);
        }
    }

    private void inicializarCardapio() {
        // Comidas
        adicionarComida("Filé à Parmegiana", 42.90, "Filé de frango empanado coberto com molho de tomate e queijo gratinado",
            new String[]{"Filé de frango", "Farinha de rosca", "Ovos", "Molho de tomate", "Queijo muçarela"});
        adicionarComida("Picanha na Chapa", 69.90, "Picanha grelhada acompanhada de arroz, feijão e vinagrete",
            new String[]{"Picanha", "Alho", "Sal grosso", "Limão"});
        adicionarComida("Frango Grelhado", 34.90, "Peito de frango grelhado com ervas finas e acompanhamentos",
            new String[]{"Peito de frango", "Alecrim", "Tomilho", "Azeite", "Alho"});
        adicionarComida("Moqueca de Peixe", 52.00, "Peixe cozido em leite de coco com dendê e legumes",
            new String[]{"Peixe", "Leite de coco", "Dendê", "Coentro", "Tomate", "Cebola"});
        adicionarComida("Macarrão ao Molho Bolonhesa", 29.90, "Macarrão espaguete com molho à base de carne moída",
            new String[]{"Espaguete", "Carne moída", "Molho de tomate", "Cebola", "Alho"});
        adicionarComida("Salada Caesar", 22.50, "Salada com alface romana, croutons, parmesão e molho caesar",
            new String[]{"Alface romana", "Croutons", "Queijo parmesão", "Molho caesar", "Limão"});
        adicionarComida("Hambúrguer Artesanal", 38.00, "Hambúrguer 180g com queijo, alface, tomate e molho especial",
            new String[]{"Pão brioche", "Hambúrguer bovino 180g", "Queijo cheddar", "Alface", "Tomate", "Molho especial"});
        adicionarComida("Risoto de Camarão", 58.00, "Risoto cremoso com camarões frescos e manjericão",
            new String[]{"Arroz arbóreo", "Camarão", "Cebola", "Vinho branco", "Creme de leite", "Parmesão"});

        // Bebidas
        adicionarBebida("Água Mineral", 4.50, "Crystal", "500ml");
        adicionarBebida("Água com Gás", 5.50, "Perrier", "330ml");
        adicionarBebida("Suco de Laranja", 9.90, "Natural da Casa", "400ml");
        adicionarBebida("Suco de Açaí", 12.90, "Natural da Casa", "400ml");
        adicionarBebida("Refrigerante Coca-Cola", 7.00, "Coca-Cola Brasil", "350ml");
        adicionarBebida("Refrigerante Guaraná", 6.00, "Ambev", "350ml");
        adicionarBebida("Cerveja Artesanal IPA", 16.00, "Cervejaria Bodebrown", "473ml");
        adicionarBebida("Vinho Tinto Suave", 28.00, "Miolo", "Taça 150ml");
        adicionarBebida("Caipirinha", 18.00, "Bar da Casa", "300ml");
        adicionarBebida("Café Expresso", 6.50, "Illy", "50ml");
    }

    // ======================== MÉTODOS DE ACESSO ========================

    public List<Mesa> getListaMesas() { return listaMesas; }
    public List<Cliente> getListaClientes() { return listaClientes; }
    public List<ItemCardapio> getListaCardapio() { return listaCardapio; }
    public List<Pedido> getListaPedidosAtivos() { return listaPedidosAtivos; }
    public List<Pedido> getListaPedidosFinalizados() { return listaPedidosFinalizados; }

    // ======================== MESAS ========================

    public void abrirMesa(int numeroMesa, Cliente cliente) throws IllegalStateException {
        if (cliente == null) throw new IllegalStateException("Cliente não pode ser nulo");
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa == null) throw new IllegalStateException("Mesa " + numeroMesa + " não encontrada");
        if (!mesa.getStatus().equals(StatusMesa.LIVRE))
            throw new IllegalStateException("Mesa " + numeroMesa + " já está ocupada");

        Pedido novoPedido = new Pedido();
        novoPedido.setMesa(mesa);
        novoPedido.setCliente(cliente);
        novoPedido.setStatus("ABERTO");

        mesa.setClienteAtual(cliente);
        mesa.ocuparMesa(novoPedido);
        listaPedidosAtivos.add(novoPedido);
        cliente.adicionarPedidoAoHistorico(novoPedido);
    }

    public void fecharMesa(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa != null && mesa.getPedidoAtual() != null) {
            listaPedidosAtivos.remove(mesa.getPedidoAtual());
            mesa.liberarMesa();
        }
    }

    public Mesa buscarMesaPorNumero(int numero) {
        for (Mesa mesa : listaMesas)
            if (mesa.getNumero() == numero) return mesa;
        return null;
    }

    public Cliente obterClienteDaMesa(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        return mesa != null ? mesa.getClienteAtual() : null;
    }

    public void associarClienteAMesa(int numeroMesa, Cliente cliente) throws IllegalStateException {
        if (cliente == null) throw new IllegalStateException("Cliente não pode ser nulo");
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa == null) throw new IllegalStateException("Mesa não encontrada");
        if (mesa.getPedidoAtual() == null) throw new IllegalStateException("Mesa não tem pedido aberto");
        mesa.setClienteAtual(cliente);
        mesa.getPedidoAtual().setCliente(cliente);
    }

    // ======================== CARDÁPIO ========================

    public void adicionarComida(String nome, double preco, String descricao, String[] ingredientes) {
        Comida novaComida = new Comida();
        novaComida.setNome(nome);
        novaComida.setPreco(preco);
        novaComida.setDescricao(descricao);
        ArrayList<String> ings = new ArrayList<>();
        if (ingredientes != null) for (String i : ingredientes) ings.add(i);
        novaComida.setIngredientes(ings);
        listaCardapio.add(novaComida);
    }

    public void adicionarComida(String nome, double preco, String descricao) {
        adicionarComida(nome, preco, descricao, null);
    }

    public void adicionarBebida(String nome, double preco, String fornecedor, String volume) {
        Bebida novaBebida = new Bebida(nome, preco, fornecedor, volume);
        listaCardapio.add(novaBebida);
    }

    public void removerItemCardapio(ItemCardapio item) { listaCardapio.remove(item); }

    public void alterarItemCardapio(ItemCardapio item, String novoNome, double novoPreco, String novaSpec) {
        item.setNome(novoNome);
        item.setPreco(novoPreco);
        if (item instanceof Comida) ((Comida) item).setDescricao(novaSpec);
        else if (item instanceof Bebida) ((Bebida) item).setVolume(novaSpec);
    }

    // ======================== CLIENTES ========================

    public void cadastrarCliente(String nome, String cpf, String email) {
        // Remove formatação do CPF
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setCpf(cpfLimpo);
        novoCliente.setEmail(email);
        novoCliente.setBonus(0.0);
        listaClientes.add(novoCliente);
    }

    public void alterarCliente(Cliente cliente, String novoNome, String novoEmail) {
        cliente.setNome(novoNome);
        cliente.setEmail(novoEmail);
    }

    public void removerCliente(Cliente cliente) { listaClientes.remove(cliente); }

    public Cliente buscarClientePorCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        for (Cliente c : listaClientes)
            if (c.getCpf().equals(cpfLimpo)) return c;
        return null;
    }

    public Cliente buscarClientePorNome(String nome) {
        for (Cliente c : listaClientes)
            if (c.getNome().equalsIgnoreCase(nome)) return c;
        return null;
    }

    // ======================== PEDIDOS ========================

    public void adicionarItemAoPedido(int numeroMesa, ItemCardapio item, int quantidade) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa == null) throw new IllegalStateException("Mesa não encontrada");
        Pedido pedido = mesa.getPedidoAtual();
        if (pedido == null) throw new IllegalStateException("Mesa não tem pedido aberto");
        pedido.adicionarItem(new ItemPedido(item, quantidade));
    }

    public void removerItemDoPedido(int numeroMesa, ItemCardapio item) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa != null && mesa.getPedidoAtual() != null)
            mesa.getPedidoAtual().getItens().removeIf(ip -> ip.getItem().equals(item));
    }

    public double calcularTotalPedido(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa == null || mesa.getPedidoAtual() == null) return 0.0;
        return mesa.getPedidoAtual().calcularTotal();
    }

    // Marcar item como entregue (para a cozinha)
    public void marcarItemComoEntregue(Pedido pedido, ItemPedido itemPedido) {
        itemPedido.setEntregue(true);
        // Se todos os itens foram entregues, muda status do pedido
        if (pedido.todosItensEntregues()) {
            pedido.setStatus("ENTREGUE");
        }
    }

    // Entregar pedido completo (cozinha)
    public void entregarPedido(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa == null || mesa.getPedidoAtual() == null)
            throw new IllegalStateException("Mesa sem pedido aberto");
        Pedido pedido = mesa.getPedidoAtual();
        for (ItemPedido ip : pedido.getItens()) ip.setEntregue(true);
        pedido.setStatus("ENTREGUE");
    }

    public void efetuarPagamento(int numeroMesa, String tipoPagamento, boolean usarBonus) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa == null || mesa.getPedidoAtual() == null)
            throw new IllegalStateException("Mesa não tem pedido aberto");

        Pedido pedido = mesa.getPedidoAtual();
        Cliente cliente = mesa.getClienteAtual();

        double totalOriginal = pedido.calcularTotal();
        double totalAPagar = totalOriginal;

        if (cliente != null) {
            pedido.setCliente(cliente);

            if (usarBonus && cliente.getBonus() > 0) {
                double bonus = cliente.getBonus();
                if (bonus >= totalAPagar) {
                    cliente.usarBonus(totalAPagar);
                    totalAPagar = 0;
                } else {
                    totalAPagar -= bonus;
                    cliente.usarBonus(bonus);
                }
            }

            // Acumula 10% do valor pago em bônus
            cliente.adicionarBonus(totalAPagar);
        }

        pedido.setStatus("PAGO");
        pedido.setDataHoraFechada(java.time.LocalDateTime.now());

        listaPedidosAtivos.remove(pedido);
        listaPedidosFinalizados.add(pedido);
        fecharMesa(numeroMesa);
    }

    public Pedido buscarPedidoDaMesa(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        return mesa != null ? mesa.getPedidoAtual() : null;
    }

    public Mesa buscarMesaAtivaDoCliente(Cliente cliente) {
        if (cliente == null) return null;
        for (Mesa mesa : listaMesas) {
            if (mesa.getStatus() == StatusMesa.OCUPADA
                    && mesa.getClienteAtual() != null
                    && mesa.getClienteAtual().getCpf().equals(cliente.getCpf())) {
                return mesa;
            }
        }
        return null;
    }

    // ======================== ESTATÍSTICAS ========================

    public int contarMesasOcupadas() {
        int count = 0;
        for (Mesa m : listaMesas)
            if (!m.getStatus().equals(StatusMesa.LIVRE)) count++;
        return count;
    }

    public int contarMesasLivres() { return listaMesas.size() - contarMesasOcupadas(); }

    public boolean validarItemCardapio(String nome, double preco) {
        return nome != null && !nome.trim().isEmpty() && preco > 0;
    }

    public String obterInfoMesa(int numeroMesa) {
        Mesa mesa = buscarMesaPorNumero(numeroMesa);
        if (mesa == null) return "Mesa não encontrada";
        StringBuilder info = new StringBuilder();
        info.append("Mesa ").append(mesa.getNumero()).append(" — ");
        info.append("Status: ").append(mesa.getStatus()).append(" — ");
        info.append("Cliente: ").append(mesa.getClienteAtual() != null ? mesa.getClienteAtual().getNome() : "Nenhum");
        return info.toString();
    }

    public double calcularFaturamentoTotal() {
        double total = 0;
        for (Cliente c : listaClientes) total += c.getTotalGasto();
        return total;
    }

    public double calcularTempoMedioPermanencia() {
        long totalMinutos = 0;
        int count = 0;
        for (Cliente c : listaClientes) {
            for (Pedido p : c.getHistoricoPedidos()) {
                if (p.getStatus().equals("PAGO")) {
                    totalMinutos += p.obterMinutosDePermanencia();
                    count++;
                }
            }
        }
        return count == 0 ? 0 : (double) totalMinutos / count;
    }

    public String obterTempoMedioFormatado() {
        double minutos = calcularTempoMedioPermanencia();
        long horas = (long) minutos / 60;
        long mins = (long) minutos % 60;
        return horas > 0 ? horas + "h " + mins + "m" : mins + "m";
    }
}
