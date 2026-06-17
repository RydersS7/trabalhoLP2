package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Vinculação estrita com as classes do seu pacote model
import model.Mesa;
import model.Cliente;
import model.ItemCardapio;
import model.Comida;
import model.Bebida;
import model.Pedido;
import model.ItemPedido;
import model.StatusMesa;

/**
 * MEZA - Sistema de Gestão de Restaurante
 * MainFrame.java — Integração Completa sem Abstrações Visuais
 */
public class MainFrame extends JFrame {

    // ---- MEMÓRIA DINÂMICA DO PACOTE MODEL (Substitui dados Hardcoded) ----
    private List<Mesa> listaMesas;
    private List<Cliente> listaClientes;
    private List<ItemCardapio> listaCardapio;
    private List<Pedido> listaPedidosAtivos;

    // ---- COMPONENTES GRÁFICOS COMPLETOS ----
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Elementos do Dashboard (Mesas)
    private JPanel gridMesasPanel;
    
    // Tabelas e Componentes de Dados
    private JTable tabelaCardapio;
    private DefaultTableModel modelCardapio;
    
    private JTable tabelaClientes;
    private DefaultTableModel modelClientes;
    
    private JTable tabelaPedidos;
    private DefaultTableModel modelPedidos;

    /**
     * Construtor do Frame Principal
     */
    public MainFrame() {
        // 1. Inicializa o estado do modelo de dados
        inicializarBancoDeDadosModel();
        
        // 2. Executa a montagem visual exata da interface
        initComponents();
        
        // 3. Alimenta a interface com as informações dos objetos
        atualizarExibicaoMesas();
        sincronizarTabelaCardapio();
        sincronizarTabelaClientes();
        sincronizarTabelaPedidos();
    }

    /**
     * Configura o estado inicial do sistema: 10 mesas livres carregadas no model.
     * Cardápio, Clientes e Pedidos começam completamente limpos para inserção dinâmica.
     */
    private void inicializarBancoDeDadosModel() {
        listaMesas = new ArrayList<>();
        listaClientes = new ArrayList<>();
        listaCardapio = new ArrayList<>();
        listaPedidosAtivos = new ArrayList<>();

        // Cria e adiciona exatamente as 10 mesas iniciais usando o model
        for (int i = 1; i <= 10; i++) {
            Mesa mesa = new Mesa();
            mesa.setNumero(i);
            mesa.setStatus(StatusMesa.LIVRE); // Atribui a constante string "LIVRE"
            listaMesas.add(mesa);
        }
    }

    /**
     * Inicialização e posicionamento completo de todos os componentes da janela.
     * Mantém rigorosamente o design original baseado em Sidebar e Bottom-Buttons.
     */
    private void initComponents() {
        setTitle("MEZA - Gestão de Restaurante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        // Painel Raiz da Aplicação
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Construção da Barra Superior e Lateral Esquerda
        JPanel topbar = criarComponenteTopbar();
        JPanel sidebar = criarComponenteSidebar();
        
        mainPanel.add(topbar, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Configuração do CardLayout Central para Troca de Telas
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(245, 245, 245));

        // Inicialização Estrutural das Tabelas do Sistema
        modelCardapio = new DefaultTableModel(new Object[]{"Item", "Categoria", "Preço Base", "Especificação / Atributo"}, 0);
        tabelaCardapio = configEstiloTabela(new JTable(modelCardapio));

        modelClientes = new DefaultTableModel(new Object[]{"Registro ID", "Nome Completo", "Telefone de Contato"}, 0);
        tabelaClientes = configEstiloTabela(new JTable(modelClientes));

        modelPedidos = new DefaultTableModel(new Object[]{"Mesa Origem", "Resumo dos Itens Solicitados", "Valor Total Acumulado"}, 0);
        tabelaPedidos = configEstiloTabela(new JTable(modelPedidos));

        // Injeção dos painéis construídos dentro do gerenciador de fluxo
        cardPanel.add(montarPainelDashboard(), "DashboardPanel");
        cardPanel.add(montarPainelPedidos(), "OrderPanel");
        cardPanel.add(montarPainelCardapio(), "MenuPanel");
        cardPanel.add(montarPainelClientes(), "CustomersPanel");

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Tela 1: Dashboard - Mostra os cartões visuais das mesas baseados no Model
     */
    private JPanel montarPainelDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Status das Mesas em Tempo Real");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Grid das mesas com espaçamento de layout regulado
        gridMesasPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        gridMesasPanel.setBackground(Color.WHITE);
        gridMesasPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(gridMesasPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Reconstrói o grid visual renderizando o estado atualizado de cada objeto Mesa
     */
    private void atualizarExibicaoMesas() {
        gridMesasPanel.removeAll();

        for (Mesa mesa : listaMesas) {
            RoundedPanel cardMesa = new RoundedPanel(12);
            cardMesa.setLayout(new BorderLayout());
            cardMesa.setPreferredSize(new Dimension(130, 110));
            cardMesa.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Coloração dinâmica vinculada diretamente ao valor lógico do atributo status
            if (StatusMesa.LIVRE.equals(mesa.getStatus())) {
                cardMesa.setBackground(new Color(235, 247, 235)); // Verde sutil para Livre
                cardMesa.setBorder(BorderFactory.createLineBorder(new Color(160, 215, 160), 1));
            } else {
                cardMesa.setBackground(new Color(255, 235, 230)); // Coral/Vermelho sutil para Ocupada
                cardMesa.setBorder(BorderFactory.createLineBorder(new Color(250, 170, 150), 1));
            }

            JLabel lblNum = new JLabel("MESA " + mesa.getNumero(), SwingConstants.CENTER);
            lblNum.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblNum.setForeground(new Color(50, 50, 50));

            JLabel lblStatus = new JLabel(mesa.getStatus(), SwingConstants.CENTER);
            lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblStatus.setForeground(StatusMesa.LIVRE.equals(mesa.getStatus()) ? new Color(40, 120, 40) : new Color(180, 50, 50));

            cardMesa.add(lblNum, BorderLayout.CENTER);
            cardMesa.add(lblStatus, BorderLayout.SOUTH);

            // Vincula o evento de clique na mesa para disparar a lógica do backend
            cardMesa.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    processarCliqueMesa(mesa);
                }
            });

            gridMesasPanel.add(cardMesa);
        }

        gridMesasPanel.revalidate();
        gridMesasPanel.repaint();
    }

    /**
     * Controla a abertura de novas contas nas mesas alterando seu estado interno
     */
    private void processarCliqueMesa(Mesa mesa) {
        if (StatusMesa.LIVRE.equals(mesa.getStatus())) {
            int resposta = JOptionPane.showConfirmDialog(this, 
                    "Deseja iniciar uma nova conta para a Mesa " + mesa.getNumero() + "?", 
                    "Abertura de Mesa", JOptionPane.YES_NO_OPTION);
            
            if (resposta == JOptionPane.YES_OPTION) {
                mesa.setStatus(StatusMesa.OCUPADA);
                
                // Cria e anexa o novo objeto Pedido
                Pedido novoPedido = new Pedido();
                novoPedido.setMesa(mesa);
                novoPedido.setStatus("ABERTO");
                mesa.setPedidoAtual(novoPedido);
                
                listaPedidosAtivos.add(novoPedido);
                
                atualizarExibicaoMesas();
                sincronizarTabelaPedidos();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                    "A Mesa " + mesa.getNumero() + " já está ativa. Gerencie o consumo na aba de Pedidos.", 
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Tela 2: Pedidos Ativos
     */
    private JPanel montarPainelPedidos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Monitor de Contas e Pedidos Ativos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.add(titulo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabelaPedidos);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)));
        panel.add(scroll, BorderLayout.CENTER);

        // CONTAINER INFERIOR: Mantém o botão de ação posicionado exatamente embaixo
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomContainer.setBackground(Color.WHITE);

        JButton btnLancarItem = new JButton("+ Adicionar Item a uma Mesa");
        estilizarBotaoAcaoInferior(btnLancarItem);
        
        btnLancarItem.addActionListener(e -> executarLancamentoItemPedido());
        bottomContainer.add(btnLancarItem);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Interpola a ação do botão inferior lançando um produto do cardápio em um pedido ativo
     */
    private void executarLancamentoItemPedido() {
        if (listaPedidosAtivos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não existem mesas ocupadas com contas abertas no momento.");
            return;
        }
        if (listaCardapio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O cardápio está vazio. Cadastre itens na aba Cardápio primeiro.");
            return;
        }

        // 1. Seleção da Mesa Alvo
        String[] mesasAtivas = new String[listaPedidosAtivos.size()];
        for (int i = 0; i < listaPedidosAtivos.size(); i++) {
            mesasAtivas[i] = "Mesa " + listaPedidosAtivos.get(i).getMesa().getNumero();
        }
        String mesaSelecionada = (String) JOptionPane.showInputDialog(this, "Selecione a mesa destino:", 
                "Lançar Item", JOptionPane.QUESTION_MESSAGE, null, mesasAtivas, mesasAtivas[0]);
        if (mesaSelecionada == null) return;
        int idxPedido = java.util.Arrays.asList(mesasAtivas).indexOf(mesaSelecionada);
        Pedido pedidoAlvo = listaPedidosAtivos.get(idxPedido);

        // 2. Seleção do Produto do Cardápio
        String[] produtosCardapio = new String[listaCardapio.size()];
        for (int i = 0; i < listaCardapio.size(); i++) {
            produtosCardapio[i] = listaCardapio.get(i).getNome() + " (R$ " + listaCardapio.get(i).getPreco() + ")";
        }
        String produtoSelecionado = (String) JOptionPane.showInputDialog(this, "Selecione o item do cardápio:", 
                "Lançar Item", JOptionPane.QUESTION_MESSAGE, null, produtosCardapio, produtosCardapio[0]);
        if (produtoSelecionado == null) return;
        int idxItem = java.util.Arrays.asList(produtosCardapio).indexOf(produtoSelecionado);
        ItemCardapio itemAlvo = listaCardapio.get(idxItem);

        // 3. Quantidade do Item
        String qtdStr = JOptionPane.showInputDialog(this, "Informe a quantidade desejada:", "1");
        if (qtdStr == null || qtdStr.trim().isEmpty()) return;
        int quantidade = Integer.parseInt(qtdStr.trim());

        // Cria a instância do ItemPedido associando ao Pedido principal
        ItemPedido novoItemPedido = new ItemPedido();
        novoItemPedido.setItem(itemAlvo);
        novoItemPedido.setQuantidade(quantidade);

        if (pedidoAlvo.getItens() == null) {
            pedidoAlvo.setItens(new ArrayList<>());
        }
        pedidoAlvo.getItens().add(novoItemPedido);

        sincronizarTabelaPedidos();
        JOptionPane.showMessageDialog(this, "Item adicionado com sucesso ao pedido da mesa!");
    }

    /**
     * Tela 3: Cardápio de Itens
     */
    private JPanel montarPainelCardapio() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Cardápio de Alimentos e Bebidas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.add(titulo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabelaCardapio);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)));
        panel.add(scroll, BorderLayout.CENTER);

        // CONTAINER INFERIOR: Botão "+" posicionado perfeitamente na parte de baixo
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomContainer.setBackground(Color.WHITE);

        JButton btnAddCardapio = new JButton("+ Adicionar ao Cardápio");
        estilizarBotaoAcaoInferior(btnAddCardapio);
        
        btnAddCardapio.addActionListener(e -> executarInclusaoCardapio());
        bottomContainer.add(btnAddCardapio);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Captura os dados da interface e adiciona instâncias de Comida ou Bebida no Model
     */
    private void executarInclusaoCardapio() {
        String[] categorias = {"Comida", "Bebida"};
        int tipoSelec = JOptionPane.showOptionDialog(this, "Selecione a categoria do novo item:", 
                "Cadastro de Cardápio", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                null, categorias, categorias[0]);

        if (tipoSelec == -1) return;

        String nome = JOptionPane.showInputDialog(this, "Nome do item do cardápio:");
        if (nome == null || nome.trim().isEmpty()) return;

        String precoStr = JOptionPane.showInputDialog(this, "Preço Unitário (Ex: 34.50):");
        if (precoStr == null || precoStr.trim().isEmpty()) return;
        double preco = Double.parseDouble(precoStr.replace(",", "."));

        if (tipoSelec == 0) { // Criação do Objeto Comida
            String descricao = JOptionPane.showInputDialog(this, "Descrição dos Ingredientes do Prato:");
            
            Comida novaComida = new Comida();
            novaComida.setNome(nome);
            novaComida.setPreco(preco);
            novaComida.setDescricao(descricao);
            novaComida.setIngredientes(new ArrayList<>()); // Inicializa a lista do model
            
            listaCardapio.add(novaComida);
        } else { // Criação do Objeto Bebida
            String fornecedor = JOptionPane.showInputDialog(this, "Nome da Empresa Fornecedora:");
            String volume = JOptionPane.showInputDialog(this, "Volume/Tamanho da embalagem (Ex: 350ml):");
            
            Bebida novaBebida = new Bebida();
            novaBebida.setNome(nome);
            novaBebida.setPreco(preco);
            novaBebida.setFornecedor(fornecedor);
            novaBebida.setVolume(volume);
            
            listaCardapio.add(novaBebida);
        }

        sincronizarTabelaCardapio();
    }

    /**
     * Tela 4: Painel de Clientes
     */
    private JPanel montarPainelClientes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Controle de Clientes Cadastrados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.add(titulo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabelaClientes);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)));
        panel.add(scroll, BorderLayout.CENTER);

        // CONTAINER INFERIOR: Botão "+" posicionado perfeitamente na parte de baixo
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomContainer.setBackground(Color.WHITE);

        JButton btnAddCliente = new JButton("+ Adicionar Novo Cliente");
        estilizarBotaoAcaoInferior(btnAddCliente);
        
        btnAddCliente.addActionListener(e -> executarInclusaoCliente());
        bottomContainer.add(btnAddCliente);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Instancia dinamicamente um objeto Cliente a partir dos dados preenchidos
     */
    private void executarInclusaoCliente() {
        String nome = JOptionPane.showInputDialog(this, "Nome completo do Cliente:");
        if (nome == null || nome.trim().isEmpty()) return;

        String cpf = JOptionPane.showInputDialog(this, "Insera o CPF (Apenas números):");
        String email = JOptionPane.showInputDialog(this, "Endereço de E-mail:");

        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setCpf(cpf);
        novoCliente.setEmail(email);
        novoCliente.setBonus(0.0); // Inicia com o bônus zerado conforme o ciclo de negócio

        listaClientes.add(novoCliente);
        sincronizarTabelaClientes();
    }

    // ---- MÉTODOS DE EXTRAÇÃO E RE-RENDERIZAÇÃO DE DADOS (Puxando do Model) ----

    private void sincronizarTabelaCardapio() {
        modelCardapio.setRowCount(0);
        for (ItemCardapio item : listaCardapio) {
            String categoria = (item instanceof Comida) ? "Comida Prato" : "Bebida";
            String especificacao = "";
            
            if (item instanceof Comida) {
                especificacao = "Ingredientes: " + ((Comida) item).getDescricao();
            } else if (item instanceof Bebida) {
                especificacao = "Vol: " + ((Bebida) item).getVolume() + " | Fornecedor: " + ((Bebida) item).getFornecedor();
            }
            
            modelCardapio.addRow(new Object[]{item.getNome(), categoria, "R$ " + String.format("%.2f", item.getPreco()), especificacao});
        }
    }

    private void sincronizarTabelaClientes() {
        modelClientes.setRowCount(0);
        for (int i = 0; i < listaClientes.size(); i++) {
            Cliente c = listaClientes.get(i);
            modelClientes.addRow(new Object[]{
                "REG-" + (i + 101), 
                c.getNome(), 
                "E-mail: " + c.getEmail() + " | Bônus: R$ " + String.format("%.2f", c.getBonus())
            });
        }
    }

    private void sincronizarTabelaPedidos() {
        modelPedidos.setRowCount(0);
        for (Pedido p : listaPedidosAtivos) {
            StringBuilder descItens = new StringBuilder();
            double totalConta = 0;

            if (p.getItens() != null && !p.getItens().isEmpty()) {
                for (ItemPedido ip : p.getItens()) {
                    descItens.append(ip.getItem().getNome())
                             .append(" [x").append(ip.getQuantidade()).append("]  ");
                    totalConta += ip.getItem().getPreco() * ip.getQuantidade();
                }
            } else {
                descItens.append("Nenhum prato/bebida consumido ainda.");
            }

            modelPedidos.addRow(new Object[]{
                "Mesa Número " + p.getMesa().getNumero(),
                descItens.toString(),
                "R$ " + String.format("%.2f", totalConta)
            });
        }
    }

    // ---- CONSTRUÇÃO E ESTILIZAÇÃO MANTIDAS FIÉIS AO SEU PROJETO ----

    private JPanel criarComponenteSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(210, 600));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        String[] botoesMenu = {"Dashboard", "Pedidos Ativos", "Cardápio", "Clientes"};
        String[] chavesCards = {"DashboardPanel", "OrderPanel", "MenuPanel", "CustomersPanel"};

        for (int i = 0; i < botoesMenu.length; i++) {
            final String linkCard = chavesCards[i];
            JButton btn = new JButton(botoesMenu[i]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(190, 42));
            btn.setForeground(new Color(230, 230, 230));
            btn.setBackground(new Color(45, 45, 45));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            
            btn.addActionListener(e -> cardLayout.show(cardPanel, linkCard));
            
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }
        return sidebar;
    }

    private JPanel criarComponenteTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(new Color(20, 20, 20));
        topbar.setPreferredSize(new Dimension(1024, 55));
        
        JLabel tituloGeral = new JLabel("  MEZA - Módulo Interno de Atendimento");
        tituloGeral.setForeground(Color.WHITE);
        tituloGeral.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        topbar.add(tituloGeral, BorderLayout.WEST);
        return topbar;
    }

    private void estilizarBotaoAcaoInferior(JButton botao) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(0, 122, 255)); // Azul padrão macOS/iOS das capturas
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JTable configEstiloTabela(JTable tabela) {
        tabela.setRowHeight(28);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(240, 240, 240));
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));
        return tabela;
    }

    // Painel Customizado para Cantos Arredondados usado no Dashboard
    static class RoundedPanel extends JPanel {
        private int raioCurvatura;
        public RoundedPanel(int raio) {
            this.raioCurvatura = raio;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raioCurvatura, raioCurvatura);
        }
    }
}