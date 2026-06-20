package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import controller.RestauranteController;
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
 * MainFrame.java — Interface Gráfica que chama o Controller
 */
public class MainFrame extends JFrame {

    // ---- REFERÊNCIA AO CONTROLLER ----
    private RestauranteController controller;

    // ---- COMPONENTES GRÁFICOS ----
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel gridMesasPanel;
    
    private JTable tabelaCardapio;
    private DefaultTableModel modelCardapio;
    
    private JTable tabelaClientes;
    private DefaultTableModel modelClientes;
    
    private JTable tabelaPedidos;
    private DefaultTableModel modelPedidos;

    /**
     * Construtor que recebe o controller
     */
    public MainFrame(RestauranteController controller) {
        this.controller = controller;
        
        // 1. Monta a interface
        initComponents();
        
        // 2. Carrega os dados iniciais
        atualizarTodos();
    }

    /**
     * Inicializa todos os componentes visuais
     */
    private void initComponents() {
        setTitle("MEZA - Gestão de Restaurante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Barra superior e sidebar
        JPanel topbar = criarComponenteTopbar();
        JPanel sidebar = criarComponenteSidebar();
        
        mainPanel.add(topbar, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);

        // CardLayout para as diferentes telas
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(245, 245, 245));

        // Inicializa as tabelas
        modelCardapio = new DefaultTableModel(new Object[]{"Item", "Categoria", "Preço Base", "Especificação"}, 0);
        tabelaCardapio = configEstiloTabela(new JTable(modelCardapio));

        modelClientes = new DefaultTableModel(new Object[]{"Registro ID", "Nome Completo", "Email e Bônus"}, 0);
        tabelaClientes = configEstiloTabela(new JTable(modelClientes));

        modelPedidos = new DefaultTableModel(new Object[]{"Mesa Origem", "Itens Solicitados", "Valor Total"}, 0);
        tabelaPedidos = configEstiloTabela(new JTable(modelPedidos));

        // Adiciona as telas ao CardLayout
        cardPanel.add(montarPainelDashboard(), "DashboardPanel");
        cardPanel.add(montarPainelPedidos(), "OrderPanel");
        cardPanel.add(montarPainelCardapio(), "MenuPanel");
        cardPanel.add(montarPainelClientes(), "CustomersPanel");

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Atualiza todos os dados exibidos
     */
    private void atualizarTodos() {
        atualizarExibicaoMesas();
        sincronizarTabelaCardapio();
        sincronizarTabelaClientes();
        sincronizarTabelaPedidos();
    }

    // ======================== DASHBOARD (Mesas) ========================

    private JPanel montarPainelDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Status das Mesas em Tempo Real");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        panel.add(lblTitulo, BorderLayout.NORTH);

        gridMesasPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        gridMesasPanel.setBackground(Color.WHITE);
        gridMesasPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(gridMesasPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void atualizarExibicaoMesas() {
        gridMesasPanel.removeAll();

        for (Mesa mesa : controller.getListaMesas()) {
            RoundedPanel cardMesa = new RoundedPanel(12);
            cardMesa.setLayout(new BorderLayout());
            cardMesa.setPreferredSize(new Dimension(130, 110));
            cardMesa.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Coloração baseada no status
            if (mesa.getStatus().equals(StatusMesa.LIVRE)) {
                cardMesa.setBackground(new Color(235, 247, 235));
                cardMesa.setBorder(BorderFactory.createLineBorder(new Color(160, 215, 160), 1));
            } else {
                cardMesa.setBackground(new Color(255, 235, 230));
                cardMesa.setBorder(BorderFactory.createLineBorder(new Color(250, 170, 150), 1));
            }

            JLabel lblNum = new JLabel("MESA " + mesa.getNumero(), SwingConstants.CENTER);
            lblNum.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblNum.setForeground(new Color(50, 50, 50));

            JLabel lblStatus = new JLabel(mesa.getStatus().toString(), SwingConstants.CENTER);
            lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblStatus.setForeground(mesa.getStatus().equals(StatusMesa.LIVRE) ? new Color(40, 120, 40) : new Color(180, 50, 50));

            cardMesa.add(lblNum, BorderLayout.CENTER);
            cardMesa.add(lblStatus, BorderLayout.SOUTH);

            // Clique para abrir mesa
            final Mesa mesaFinal = mesa;
            cardMesa.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    processarCliqueMesa(mesaFinal);
                }
            });

            gridMesasPanel.add(cardMesa);
        }

        gridMesasPanel.revalidate();
        gridMesasPanel.repaint();
    }

    private void processarCliqueMesa(Mesa mesa) {
        if (mesa.getStatus().equals(StatusMesa.LIVRE)) {
            int resposta = JOptionPane.showConfirmDialog(this, 
                    "Deseja iniciar uma nova conta para a Mesa " + mesa.getNumero() + "?", 
                    "Abertura de Mesa", JOptionPane.YES_NO_OPTION);
            
            if (resposta == JOptionPane.YES_OPTION) {
                try {
                    controller.abrirMesa(mesa.getNumero(),mesa.getClienteAtual());
                    atualizarTodos();
                    JOptionPane.showMessageDialog(this, "Mesa " + mesa.getNumero() + " aberta com sucesso!");
                } catch (IllegalStateException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                    "A Mesa " + mesa.getNumero() + " já está ativa. Gerencie o consumo na aba de Pedidos.", 
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ======================== PEDIDOS ========================

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

        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomContainer.setBackground(Color.WHITE);

        JButton btnLancarItem = new JButton("+ Adicionar Item a uma Mesa");
        estilizarBotaoAcaoInferior(btnLancarItem);
        btnLancarItem.addActionListener(e -> executarLancamentoItemPedido());
        
        bottomContainer.add(btnLancarItem);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private void executarLancamentoItemPedido() {
        if (controller.getListaPedidosAtivos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há mesas com contas abertas.");
            return;
        }
        if (controller.getListaCardapio().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O cardápio está vazio. Cadastre itens primeiro.");
            return;
        }

        // Seleção da mesa
        String[] mesasAtivas = new String[controller.getListaPedidosAtivos().size()];
        for (int i = 0; i < controller.getListaPedidosAtivos().size(); i++) {
            mesasAtivas[i] = "Mesa " + controller.getListaPedidosAtivos().get(i).getMesa().getNumero();
        }
        String mesaSelecionada = (String) JOptionPane.showInputDialog(this, "Selecione a mesa:", 
                "Lançar Item", JOptionPane.QUESTION_MESSAGE, null, mesasAtivas, mesasAtivas[0]);
        if (mesaSelecionada == null) return;

        int numMesa = Integer.parseInt(mesaSelecionada.replaceAll("[^0-9]", ""));

        // Seleção do item
        String[] itens = new String[controller.getListaCardapio().size()];
        for (int i = 0; i < controller.getListaCardapio().size(); i++) {
            itens[i] = controller.getListaCardapio().get(i).getNome() + " (R$ " + 
                       controller.getListaCardapio().get(i).getPreco() + ")";
        }
        String itemSelecionado = (String) JOptionPane.showInputDialog(this, "Selecione o item:", 
                "Lançar Item", JOptionPane.QUESTION_MESSAGE, null, itens, itens[0]);
        if (itemSelecionado == null) return;

        int idxItem = java.util.Arrays.asList(itens).indexOf(itemSelecionado);
        ItemCardapio item = controller.getListaCardapio().get(idxItem);

        // Quantidade
        String qtdStr = JOptionPane.showInputDialog(this, "Quantidade:", "1");
        if (qtdStr == null) return;
        int quantidade = Integer.parseInt(qtdStr);

        try {
            controller.adicionarItemAoPedido(numMesa, item, quantidade);
            atualizarTodos();
            JOptionPane.showMessageDialog(this, "Item adicionado com sucesso!");
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ======================== CARDÁPIO ========================

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

        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomContainer.setBackground(Color.WHITE);

        JButton btnAddCardapio = new JButton("+ Adicionar ao Cardápio");
        estilizarBotaoAcaoInferior(btnAddCardapio);
        btnAddCardapio.addActionListener(e -> executarInclusaoCardapio());
        
        bottomContainer.add(btnAddCardapio);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private void executarInclusaoCardapio() {
        String[] categorias = {"Comida", "Bebida"};
        int tipoSelec = JOptionPane.showOptionDialog(this, "Selecione a categoria:", 
                "Cadastro de Cardápio", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                null, categorias, categorias[0]);

        if (tipoSelec == -1) return;

        String nome = JOptionPane.showInputDialog(this, "Nome do item:");
        if (nome == null || nome.trim().isEmpty()) return;

        String precoStr = JOptionPane.showInputDialog(this, "Preço Unitário:");
        if (precoStr == null) return;
        double preco = Double.parseDouble(precoStr.replace(",", "."));

        if (tipoSelec == 0) {
            String descricao = JOptionPane.showInputDialog(this, "Descrição dos Ingredientes:");
            controller.adicionarComida(nome, preco, descricao);
        } else {
            String fornecedor = JOptionPane.showInputDialog(this, "Fornecedor:");
            String volume = JOptionPane.showInputDialog(this, "Volume/Tamanho:");
            controller.adicionarBebida(nome, preco, fornecedor, volume);
        }

        atualizarTodos();
        JOptionPane.showMessageDialog(this, "Item adicionado ao cardápio!");
    }

    // ======================== CLIENTES ========================

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

        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomContainer.setBackground(Color.WHITE);

        JButton btnAddCliente = new JButton("+ Adicionar Novo Cliente");
        estilizarBotaoAcaoInferior(btnAddCliente);
        btnAddCliente.addActionListener(e -> executarInclusaoCliente());
        
        bottomContainer.add(btnAddCliente);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private void executarInclusaoCliente() {
        String nome = JOptionPane.showInputDialog(this, "Nome completo:");
        if (nome == null || nome.trim().isEmpty()) return;

        String cpf = JOptionPane.showInputDialog(this, "CPF (apenas números):");
        String email = JOptionPane.showInputDialog(this, "Email:");

        controller.cadastrarCliente(nome, cpf, email);
        atualizarTodos();
        JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
    }

    // ======================== SINCRONIZAÇÃO DE TABELAS ========================

    private void sincronizarTabelaCardapio() {
        modelCardapio.setRowCount(0);
        for (ItemCardapio item : controller.getListaCardapio()) {
            String categoria = (item instanceof Comida) ? "Comida" : "Bebida";
            String especificacao = "";
            
            if (item instanceof Comida) {
                especificacao = "Ingredientes: " + ((Comida) item).getDescricao();
            } else if (item instanceof Bebida) {
                especificacao = "Vol: " + ((Bebida) item).getVolume() + " | Fornecedor: " + ((Bebida) item).getFornecedor();
            }
            
            modelCardapio.addRow(new Object[]{item.getNome(), categoria, "R$ " + 
                                String.format("%.2f", item.getPreco()), especificacao});
        }
    }

    private void sincronizarTabelaClientes() {
        modelClientes.setRowCount(0);
        for (int i = 0; i < controller.getListaClientes().size(); i++) {
            Cliente c = controller.getListaClientes().get(i);
            modelClientes.addRow(new Object[]{
                "REG-" + (i + 101), 
                c.getNome(), 
                "E-mail: " + c.getEmail() + " | Bônus: R$ " + String.format("%.2f", c.getBonus())
            });
        }
    }

    private void sincronizarTabelaPedidos() {
        modelPedidos.setRowCount(0);
        for (Pedido p : controller.getListaPedidosAtivos()) {
            StringBuilder descItens = new StringBuilder();
            double totalConta = 0;

            if (p.getItens() != null && !p.getItens().isEmpty()) {
                for (ItemPedido ip : p.getItens()) {
                    descItens.append(ip.getItem().getNome())
                             .append(" [x").append(ip.getQuantidade()).append("]  ");
                    totalConta += ip.getItem().getPreco() * ip.getQuantidade();
                }
            } else {
                descItens.append("Nenhum item consumido ainda.");
            }

            modelPedidos.addRow(new Object[]{
                "Mesa Número " + p.getMesa().getNumero(),
                descItens.toString(),
                "R$ " + String.format("%.2f", totalConta)
            });
        }
    }

    // ======================== COMPONENTES VISUAIS ========================

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
            btn.setForeground(Color.BLACK);
            btn.setBackground(new Color(45, 45, 45));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            
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
        botao.setForeground(Color.BLACK);
        botao.setBackground(new Color(0, 122, 255));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
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

    // Painel com cantos arredondados
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