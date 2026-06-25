package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

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
 * MEZA - Sistema de Gestão de Restaurante (Versão 2 - Refatorada)
 * MainFrame.java — Interface Gráfica com novo design
 */
public class MainFrame extends JFrame {

    private RestauranteController controller;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Componentes Dashboard
    private JPanel gridMesasPanel;
    private JLabel lblMesasOcupadas;
    private JLabel lblMesasLivres;
    private JLabel lblFaturamento;
    private JLabel lblTempoMedio;
    
    // Componentes Cardápio
    private JPanel painelCardapioItens;
    private JTabbedPane abasCardapio;
    
    // Componentes Cliente
    private JLabel lblNomeCliente;
    private JLabel lblCPFCliente;
    private JLabel lblEmailCliente;
    private JLabel lblBonusCliente;
    private JLabel lblTotalGastoCliente;
    private JPanel painelHistoricoPedidos;

    public MainFrame(RestauranteController controller) {
        this.controller = controller;
        initComponents();
        atualizarTodos();
    }

    private void initComponents() {
        setTitle("MEZA - Gestão de Restaurante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Topbar
        JPanel topbar = criarTopbar();
        mainPanel.add(topbar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = criarSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(240, 240, 240));

        cardPanel.add(montarPainelDashboard(), "Dashboard");
        cardPanel.add(montarPainelCardapio(), "Cardapio");
        cardPanel.add(montarPainelClientes(), "Clientes");

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    // ======================== DASHBOARD ========================

    private JPanel montarPainelDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("Visão Geral");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        // Painel com métricas
        JPanel panelMetricas = criarPainelMetricas();
        
        // Painel com grid de mesas
        JPanel panelMesas = new JPanel(new BorderLayout());
        panelMesas.setBackground(new Color(245, 245, 245));
        panelMesas.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel lblMesas = new JLabel("Mesas");
        lblMesas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelMesas.add(lblMesas, BorderLayout.NORTH);

        gridMesasPanel = new JPanel(new GridLayout(0, 5, 15, 15));
        gridMesasPanel.setBackground(new Color(245, 245, 245));
        
        JScrollPane scrollMesas = new JScrollPane(gridMesasPanel);
        scrollMesas.setBorder(BorderFactory.createEmptyBorder());
        scrollMesas.getVerticalScrollBar().setUnitIncrement(20);
        panelMesas.add(scrollMesas, BorderLayout.CENTER);

        // Adiciona tudo ao painel principal
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(new Color(245, 245, 245));
        topSection.add(panelMetricas, BorderLayout.NORTH);
        topSection.add(panelMesas, BorderLayout.CENTER);

        panel.add(topSection, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarPainelMetricas() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(new Color(245, 245, 245));
        panel.setPreferredSize(new Dimension(0, 120));

        // Card Mesas Ocupadas
        JPanel cardOcupadas = criarCardMetrica("Mesas ocupadas", "");
        lblMesasOcupadas = (JLabel) cardOcupadas.getComponent(1);
        panel.add(cardOcupadas);

        // Card Mesas Livres
        JPanel cardLivres = criarCardMetrica("Mesas livres", "");
        lblMesasLivres = (JLabel) cardLivres.getComponent(1);
        panel.add(cardLivres);

        // Card Faturamento
        JPanel cardFatura = criarCardMetrica("Faturamento hoje", "");
        lblFaturamento = (JLabel) cardFatura.getComponent(1);
        panel.add(cardFatura);

        // Card Tempo Médio
        JPanel cardTempo = criarCardMetrica("Tempo médio", "");
        lblTempoMedio = (JLabel) cardTempo.getComponent(1);
        panel.add(cardTempo);

        return panel;
    }

    private JPanel criarCardMetrica(String titulo, String valor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(new Color(150, 150, 150));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(new Color(50, 50, 50));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private void atualizarExibicaoMesas() {
        gridMesasPanel.removeAll();

        for (Mesa mesa : controller.getListaMesas()) {
            JPanel cardMesa = criarCardMesa(mesa);
            gridMesasPanel.add(cardMesa);
        }

        gridMesasPanel.revalidate();
        gridMesasPanel.repaint();
    }

    private JPanel criarCardMesa(Mesa mesa) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(mesa.getStatus() == StatusMesa.LIVRE ? 
            new Color(230, 245, 230) : new Color(255, 240, 235));
        card.setBorder(BorderFactory.createLineBorder(
            mesa.getStatus() == StatusMesa.LIVRE ? 
            new Color(150, 200, 150) : new Color(220, 150, 120), 2
        ));
        card.setPreferredSize(new Dimension(150, 140));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblNumero = new JLabel("MESA " + mesa.getNumero());
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNumero.setHorizontalAlignment(SwingConstants.CENTER);
        lblNumero.setForeground(new Color(50, 50, 50));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(mesa.getStatus() == StatusMesa.LIVRE ? 
            new Color(230, 245, 230) : new Color(255, 240, 235));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (mesa.getClienteAtual() != null && mesa.getPedidoAtual() != null) {
            JLabel lblCliente = new JLabel(mesa.getClienteAtual().getNome());
            lblCliente.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblCliente.setHorizontalAlignment(SwingConstants.CENTER);
            
            String tempo = mesa.getPedidoAtual().obterTempoFormatado();
            JLabel lblTempo = new JLabel("⏱ " + tempo);
            lblTempo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblTempo.setHorizontalAlignment(SwingConstants.CENTER);
            lblTempo.setForeground(new Color(100, 100, 100));

            centerPanel.add(lblCliente, BorderLayout.NORTH);
            centerPanel.add(lblTempo, BorderLayout.CENTER);
        } else {
            JLabel lblStatus = new JLabel(mesa.getStatus().toString());
            lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
            lblStatus.setForeground(new Color(100, 150, 100));
            centerPanel.add(lblStatus, BorderLayout.CENTER);
        }

        card.add(lblNumero, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);

        final Mesa mesaFinal = mesa;
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                processarCliqueMesa(mesaFinal);
            }
        });

        return card;
    }

    private void processarCliqueMesa(Mesa mesa) {
        if (mesa.getStatus() == StatusMesa.LIVRE) {
            // Diálogo para seleção de cliente
            JDialog dialogCliente = new JDialog(this, "Selecionar Cliente", true);
            dialogCliente.setSize(400, 200);
            dialogCliente.setLocationRelativeTo(this);
            dialogCliente.setResizable(false);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel lbl = new JLabel("Selecione um cliente ou crie um novo:");
            panel.add(lbl, BorderLayout.NORTH);

            String[] clientes = new String[controller.getListaClientes().size() + 1];
            clientes[0] = "< Novo Cliente >";
            for (int i = 0; i < controller.getListaClientes().size(); i++) {
                clientes[i + 1] = controller.getListaClientes().get(i).getNome();
            }

            JComboBox<String> combo = new JComboBox<>(clientes);
            panel.add(combo, BorderLayout.CENTER);

            JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnOk = new JButton("Confirmar");
            JButton btnCancelar = new JButton("Cancelar");

            panelBotoes.add(btnOk);
            panelBotoes.add(btnCancelar);
            panel.add(panelBotoes, BorderLayout.SOUTH);

            btnOk.addActionListener(e -> {
                int idx = combo.getSelectedIndex();
                Cliente clienteSelecionado = null;

                if (idx == 0) {
                    // Novo cliente
                    clienteSelecionado = criarNovoCliente();
                } else {
                    clienteSelecionado = controller.getListaClientes().get(idx - 1);
                }

                if (clienteSelecionado != null) {
                    try {
                        controller.abrirMesa(mesa.getNumero(), clienteSelecionado);
                        atualizarTodos();
                        JOptionPane.showMessageDialog(MainFrame.this, 
                            "Mesa " + mesa.getNumero() + " aberta para " + clienteSelecionado.getNome());
                    } catch (IllegalStateException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
                dialogCliente.dispose();
            });

            btnCancelar.addActionListener(e -> dialogCliente.dispose());

            dialogCliente.add(panel);
            dialogCliente.setVisible(true);
        } else {
            // Mesa ocupada - mostrar opções
            JDialog dialogMesa = new JDialog(this, "Gerenciar Mesa", true);
            dialogMesa.setSize(350, 250);
            dialogMesa.setLocationRelativeTo(this);
            dialogMesa.setResizable(false);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel lbl = new JLabel("Mesa " + mesa.getNumero() + " - " + mesa.getClienteAtual().getNome());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panel.add(lbl, BorderLayout.NORTH);

            JPanel btnPanel = new JPanel(new GridLayout(3, 1, 0, 10));
            
            JButton btnAdd = new JButton("Adicionar Item");
            JButton btnFechar = new JButton("Fechar Mesa & Pagar");
            JButton btnVer = new JButton("Ver Pedido");

            btnAdd.addActionListener(e -> {
                adicionarItemAMesa(mesa);
                dialogMesa.dispose();
                atualizarTodos();
            });

            btnFechar.addActionListener(e -> {
                finalizarPagamento(mesa);
                dialogMesa.dispose();
                atualizarTodos();
            });

            btnVer.addActionListener(e -> {
                visualizarPedido(mesa);
            });

            btnPanel.add(btnAdd);
            btnPanel.add(btnVer);
            btnPanel.add(btnFechar);
            panel.add(btnPanel, BorderLayout.CENTER);

            dialogMesa.add(panel);
            dialogMesa.setVisible(true);
        }
    }

    private Cliente criarNovoCliente() {
        JDialog dialog = new JDialog(this, "Novo Cliente", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblNome = new JLabel("Nome:");
        JTextField txtNome = new JTextField();
        JLabel lblCPF = new JLabel("CPF:");
        JTextField txtCPF = new JTextField();
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField();
        
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        panel.add(lblNome);
        panel.add(txtNome);
        panel.add(lblCPF);
        panel.add(txtCPF);
        panel.add(lblEmail);
        panel.add(txtEmail);
        panel.add(btnSalvar);
        panel.add(btnCancelar);

        final Cliente[] clienteSalvo = new Cliente[1];

        btnSalvar.addActionListener(e -> {
            if (txtNome.getText().isEmpty() || txtCPF.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha os campos obrigatórios");
                return;
            }
            controller.cadastrarCliente(txtNome.getText(), txtCPF.getText(), txtEmail.getText());
            clienteSalvo[0] = controller.buscarClientePorCpf(txtCPF.getText());
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);

        return clienteSalvo[0];
    }

    private void adicionarItemAMesa(Mesa mesa) {
        if (controller.getListaCardapio().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cardápio vazio!");
            return;
        }

        String[] itens = new String[controller.getListaCardapio().size()];
        for (int i = 0; i < controller.getListaCardapio().size(); i++) {
            ItemCardapio item = controller.getListaCardapio().get(i);
            itens[i] = item.getNome() + " (R$ " + String.format("%.2f", item.getPreco()) + ")";
        }

        String itemSel = (String) JOptionPane.showInputDialog(this, "Selecione o item:",
            "Adicionar Item", JOptionPane.QUESTION_MESSAGE, null, itens, itens[0]);
        
        if (itemSel == null) return;

        int idx = java.util.Arrays.asList(itens).indexOf(itemSel);
        ItemCardapio item = controller.getListaCardapio().get(idx);

        String qtd = JOptionPane.showInputDialog(this, "Quantidade:", "1");
        if (qtd == null) return;

        try {
            int quantidade = Integer.parseInt(qtd);
            controller.adicionarItemAoPedido(mesa.getNumero(), item, quantidade);
            JOptionPane.showMessageDialog(this, "Item adicionado!");
        } catch (NumberFormatException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void visualizarPedido(Mesa mesa) {
        Pedido pedido = controller.buscarPedidoDaMesa(mesa.getNumero());
        if (pedido == null || pedido.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum item no pedido");
            return;
        }

        StringBuilder sb = new StringBuilder("PEDIDO DA MESA " + mesa.getNumero() + "\n\n");
        double total = 0;
        for (ItemPedido ip : pedido.getItens()) {
            sb.append(ip.getItem().getNome()).append(" x").append(ip.getQuantidade())
                .append(" = R$ ").append(String.format("%.2f", ip.calcularSubtotal())).append("\n");
            total += ip.calcularSubtotal();
        }
        sb.append("\nTOTAL: R$ ").append(String.format("%.2f", total));

        JOptionPane.showMessageDialog(this, sb.toString(), "Pedido", JOptionPane.INFORMATION_MESSAGE);
    }

    private void finalizarPagamento(Mesa mesa) {
        Pedido pedido = controller.buscarPedidoDaMesa(mesa.getNumero());
        if (pedido == null || pedido.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum item para pagar");
            return;
        }

        double total = pedido.calcularTotal();
        Cliente cliente = mesa.getClienteAtual();

        String msg = "RESUMO DO PAGAMENTO\n\n";
        msg += "Mesa: " + mesa.getNumero() + "\n";
        msg += "Cliente: " + (cliente != null ? cliente.getNome() : "Sem cliente") + "\n";
        msg += "Total: R$ " + String.format("%.2f", total) + "\n";
        if (cliente != null && cliente.getBonus() > 0) {
            msg += "Bônus disponível: R$ " + String.format("%.2f", cliente.getBonus()) + "\n\n";
            msg += "Deseja usar o bônus?";
        }

        int resposta = JOptionPane.showConfirmDialog(this, msg, "Pagamento", JOptionPane.YES_NO_CANCEL_OPTION);
        
        boolean usarBonus = false;
        if (resposta == JOptionPane.YES_OPTION && cliente != null && cliente.getBonus() > 0) {
            usarBonus = true;
        } else if (resposta == JOptionPane.CANCEL_OPTION) {
            return;
        }

        String[] opcoesPagamento = {"Débito", "Crédito", "Dinheiro"};
        String tipoPagamento = (String) JOptionPane.showInputDialog(this, "Forma de pagamento:",
            "Pagamento", JOptionPane.QUESTION_MESSAGE, null, opcoesPagamento, opcoesPagamento[0]);

        if (tipoPagamento != null) {
            try {
                controller.efetuarPagamento(mesa.getNumero(), tipoPagamento, usarBonus);
                JOptionPane.showMessageDialog(this, "Pagamento realizado com sucesso!");
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ======================== CARDÁPIO ========================

    private JPanel montarPainelCardapio() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Cardápio");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        abasCardapio = new JTabbedPane();
        abasCardapio.setBackground(Color.WHITE);

        JPanel abaComidas = criarAbaCardapio("Comidas", true);
        JPanel abaBebidas = criarAbaCardapio("Bebidas", false);

        abasCardapio.addTab("Comidas", abaComidas);
        abasCardapio.addTab("Bebidas", abaBebidas);

        panel.add(abasCardapio, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarAbaCardapio(String categoria, boolean ehComida) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        painelCardapioItens = new JPanel(new GridLayout(0, 3, 15, 15));
        painelCardapioItens.setBackground(new Color(245, 245, 245));
        painelCardapioItens.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(painelCardapioItens);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotao.setBackground(new Color(245, 245, 245));
        JButton btnAdd = new JButton("+ Adicionar Item");
        btnAdd.addActionListener(e -> adicionarItemCardapio(ehComida));
        panelBotao.add(btnAdd);
        panel.add(panelBotao, BorderLayout.SOUTH);

        return panel;
    }

    private void adicionarItemCardapio(boolean ehComida) {
        String tipo = ehComida ? "Comida" : "Bebida";
        
        String nome = JOptionPane.showInputDialog(this, "Nome do " + tipo + ":");
        if (nome == null || nome.isEmpty()) return;

        String preco = JOptionPane.showInputDialog(this, "Preço:");
        if (preco == null) return;

        try {
            double precoDbl = Double.parseDouble(preco.replace(",", "."));
            
            if (ehComida) {
                String descricao = JOptionPane.showInputDialog(this, "Descrição/Ingredientes:");
                controller.adicionarComida(nome, precoDbl, descricao);
            } else {
                String fornecedor = JOptionPane.showInputDialog(this, "Fornecedor:");
                String volume = JOptionPane.showInputDialog(this, "Volume:");
                controller.adicionarBebida(nome, precoDbl, fornecedor, volume);
            }
            
            atualizarTodos();
            JOptionPane.showMessageDialog(this, "Item adicionado!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarCardapio() {
        painelCardapioItens.removeAll();

        for (ItemCardapio item : controller.getListaCardapio()) {
            boolean ehComida = item instanceof Comida;
            JPanel cardItem = criarCardItem(item, ehComida);
            painelCardapioItens.add(cardItem);
        }

        painelCardapioItens.revalidate();
        painelCardapioItens.repaint();
    }

    private JPanel criarCardItem(ItemCardapio item, boolean ehComida) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(250, 200));

        JLabel lblNome = new JLabel(item.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblPreco = new JLabel("R$ " + String.format("%.2f", item.getPreco()));
        lblPreco.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPreco.setForeground(new Color(220, 100, 50));

        JLabel lblDesc = new JLabel();
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblDesc.setForeground(new Color(120, 120, 120));

        if (ehComida) {
            Comida c = (Comida) item;
            lblDesc.setText("<html>Ingredientes: " + (c.getDescricao() != null ? c.getDescricao() : "N/A") + "</html>");
        } else {
            Bebida b = (Bebida) item;
            lblDesc.setText("Vol: " + b.getVolume() + " | " + b.getFornecedor());
        }

        card.add(lblNome, BorderLayout.NORTH);
        card.add(lblPreco, BorderLayout.WEST);
        card.add(lblDesc, BorderLayout.CENTER);

        return card;
    }

    // ======================== CLIENTES ========================

    private JPanel montarPainelClientes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Clientes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        // Painel de seleção
        JPanel panelSelecao = new JPanel(new BorderLayout());
        panelSelecao.setBackground(new Color(245, 245, 245));
        panelSelecao.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lbl = new JLabel("Selecione um cliente:");
        String[] nomes = new String[controller.getListaClientes().size()];
        for (int i = 0; i < controller.getListaClientes().size(); i++) {
            nomes[i] = controller.getListaClientes().get(i).getNome();
        }
        JComboBox<String> combo = new JComboBox<>(nomes);
        combo.addActionListener(e -> {
            if (combo.getSelectedIndex() >= 0) {
                Cliente c = controller.getListaClientes().get(combo.getSelectedIndex());
                exibirClienteDetalhe(c);
            }
        });

        panelSelecao.add(lbl, BorderLayout.WEST);
        panelSelecao.add(combo, BorderLayout.CENTER);
        panel.add(panelSelecao, BorderLayout.NORTH);

        // Painel de detalhes
        JPanel panelDetalhes = new JPanel(new BorderLayout());
        panelDetalhes.setBackground(Color.WHITE);
        panelDetalhes.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        panelDetalhes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel panelInfo = new JPanel(new GridLayout(5, 1, 0, 10));
        panelInfo.setBackground(Color.WHITE);

        lblNomeCliente = new JLabel("Nome: ");
        lblCPFCliente = new JLabel("CPF: ");
        lblEmailCliente = new JLabel("Email: ");
        lblBonusCliente = new JLabel("Bônus: ");
        lblTotalGastoCliente = new JLabel("Total gasto: ");

        panelInfo.add(lblNomeCliente);
        panelInfo.add(lblCPFCliente);
        panelInfo.add(lblEmailCliente);
        panelInfo.add(lblBonusCliente);
        panelInfo.add(lblTotalGastoCliente);

        panelDetalhes.add(panelInfo, BorderLayout.NORTH);

        // Histórico
        painelHistoricoPedidos = new JPanel(new GridLayout(0, 1, 0, 10));
        painelHistoricoPedidos.setBackground(Color.WHITE);
        JScrollPane scrollHistorico = new JScrollPane(painelHistoricoPedidos);
        scrollHistorico.setBorder(BorderFactory.createEmptyBorder());

        JLabel lblHistorico = new JLabel("Histórico de Pedidos");
        lblHistorico.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JPanel panelHistoricoContainer = new JPanel(new BorderLayout());
        panelHistoricoContainer.setBackground(Color.WHITE);
        panelHistoricoContainer.add(lblHistorico, BorderLayout.NORTH);
        panelHistoricoContainer.add(scrollHistorico, BorderLayout.CENTER);

        panelDetalhes.add(panelHistoricoContainer, BorderLayout.CENTER);
        panel.add(panelDetalhes, BorderLayout.CENTER);

        // Botão novo cliente
        JPanel panelBotao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotao.setBackground(new Color(245, 245, 245));
        JButton btnNovo = new JButton("+ Novo Cliente");
        btnNovo.addActionListener(e -> {
            criarNovoCliente();
            atualizarTodos();
        });
        panelBotao.add(btnNovo);
        panel.add(panelBotao, BorderLayout.SOUTH);

        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
            exibirClienteDetalhe(controller.getListaClientes().get(0));
        }

        return panel;
    }

    private void exibirClienteDetalhe(Cliente cliente) {
        lblNomeCliente.setText("Nome: " + cliente.getNome());
        lblCPFCliente.setText("CPF: " + cliente.getCpf());
        lblEmailCliente.setText("Email: " + cliente.getEmail());
        lblBonusCliente.setText("Bônus: R$ " + String.format("%.2f", cliente.getBonus()));
        lblTotalGastoCliente.setText("Total gasto: R$ " + String.format("%.2f", cliente.getTotalGasto()));

        painelHistoricoPedidos.removeAll();
        for (Pedido pedido : cliente.getHistoricoPedidos()) {
            JPanel panelPedido = new JPanel(new BorderLayout());
            panelPedido.setBackground(new Color(250, 250, 250));
            panelPedido.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
            panelPedido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            String msg = "Mesa " + pedido.getMesa().getNumero() + " | " + 
                         pedido.getItens().size() + " itens | R$ " + 
                         String.format("%.2f", pedido.calcularTotal()) + " | Status: " + pedido.getStatus();

            JLabel lbl = new JLabel(msg);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            panelPedido.add(lbl, BorderLayout.CENTER);

            painelHistoricoPedidos.add(panelPedido);
        }

        painelHistoricoPedidos.revalidate();
        painelHistoricoPedidos.repaint();
    }

    // ======================== ATUALIZAÇÃO ========================

    private void atualizarTodos() {
        // Métricas
        lblMesasOcupadas.setText(String.valueOf(controller.contarMesasOcupadas()));
        lblMesasLivres.setText(String.valueOf(controller.contarMesasLivres()));
        lblFaturamento.setText("R$ " + String.format("%.2f", controller.calcularFaturamentoTotal()));
        lblTempoMedio.setText(controller.obterTempoMedioFormatado());

        // Mesas
        atualizarExibicaoMesas();

        // Cardápio
        atualizarCardapio();
    }

    // ======================== COMPONENTES VISUAIS ========================

    private JPanel criarTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(new Color(20, 20, 20));
        topbar.setPreferredSize(new Dimension(0, 60));

        JLabel titulo = new JLabel("  MEZA - Gestão que Flui");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        topbar.add(titulo, BorderLayout.WEST);
        return topbar;
    }

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));

        String[] botoes = {"Dashboard", "Cardápio", "Clientes"};
        String[] cards = {"Dashboard", "Cardapio", "Clientes"};

        for (int i = 0; i < botoes.length; i++) {
            final String card = cards[i];
            JButton btn = new JButton(botoes[i]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(160, 40));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(230, 100, 50));
            btn.setForeground(Color.WHITE);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> cardLayout.show(cardPanel, card));

            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }

        return sidebar;
    }
}