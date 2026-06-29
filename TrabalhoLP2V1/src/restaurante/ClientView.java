package restaurante;

import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ClientView extends JFrame {

    private RestauranteController controller;
    private Cliente cliente;
    private Mesa mesaSelecionada = null;

    // Carrinho: item -> quantidade
    private Map<ItemCardapio, Integer> carrinho = new LinkedHashMap<>();

    // Labels atualizáveis
    private JLabel bonusValLbl;
    private JLabel totalGastoValLbl;
    private JLabel pedidosValLbl;
    private JLabel carrinhoCountLbl;
    private DefaultTableModel carrinhoModel;
    private JLabel carrinhoTotalLbl;
    private DefaultTableModel histModel;
    private DefaultTableModel itensPedidoModel; // itens já enviados à cozinha
    private JLabel itensPedidoTotalLbl;

    // Mesa selection screen
    private JPanel mesaSelectionPanel;
    private JPanel mainPanel;
    private CardLayout rootCardLayout;
    private JPanel mesaGridPanel;
    private Timer mesaRefreshTimer;

    public ClientView(RestauranteController controller, Cliente cliente) {
        this.controller = controller;
        this.cliente = cliente;
        setTitle("Fogo na Chapa — Área do Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
        setResizable(true);
        initComponents();
    }

    private void initComponents() {
        rootCardLayout = new CardLayout();
        JPanel root = new JPanel(rootCardLayout);
        root.setBackground(new Color(245, 246, 250));
        setContentPane(root);

        mesaSelectionPanel = buildMesaSelectionPanel();
        mainPanel = buildMainPanel();

        root.add(mesaSelectionPanel, "mesa");
        root.add(mainPanel, "main");

        // Se o cliente já tem uma mesa ativa (voltou após logout), reconecta diretamente
        Mesa mesaAtiva = controller.buscarMesaAtivaDoCliente(cliente);
        if (mesaAtiva != null) {
            mesaSelecionada = mesaAtiva;
            rootCardLayout.show(root, "main");
            // Não inicia o timer de mesa pois já está no painel principal
        } else {
            rootCardLayout.show(root, "mesa");
            startMesaRefreshTimer();
        }
    }

    private JPanel buildMesaSelectionPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 246, 250));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(18, 18, 24));
        header.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 20));
        header.setPreferredSize(new Dimension(0, 56));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 13));
        left.setOpaque(false);
        JLabel logo = new JLabel("🍖");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 14));
        logo.setOpaque(true);
        logo.setBackground(new Color(0, 122, 255));
        logo.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        JLabel title = new JLabel("Fogo na Chapa");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel sub = new JLabel("Escolha sua Mesa");
        sub.setForeground(new Color(120, 128, 160));
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        left.add(logo); left.add(title); left.add(sub);
        header.add(left, BorderLayout.WEST);

        JPanel rightH = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        rightH.setOpaque(false);
        JLabel userLbl = new JLabel("Olá, " + cliente.getNome().split(" ")[0]);
        userLbl.setForeground(new Color(200, 205, 220));
        userLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        JButton sairBtn = miniBtn("Sair");
        sairBtn.addActionListener(e -> {
            stopMesaRefreshTimer();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen(controller).setVisible(true));
        });
        rightH.add(userLbl); rightH.add(sairBtn);
        header.add(rightH, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(245, 246, 250));
        content.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        JLabel pageTitle = new JLabel("Selecione uma Mesa");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(new Color(22, 24, 35));
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSub = new JLabel("Escolha uma mesa disponível para iniciar seu pedido. O status é atualizado em tempo real.");
        pageSub.setFont(new Font("Arial", Font.PLAIN, 12));
        pageSub.setForeground(new Color(110, 118, 145));
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(pageTitle);
        content.add(Box.createVerticalStrut(6));
        content.add(pageSub);
        content.add(Box.createVerticalStrut(28));

        mesaGridPanel = new JPanel(new GridLayout(2, 5, 16, 16));
        mesaGridPanel.setBackground(new Color(245, 246, 250));
        mesaGridPanel.setMaximumSize(new Dimension(800, 260));
        mesaGridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshMesaGrid();

        content.add(mesaGridPanel);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        legend.setBackground(new Color(245, 246, 250));
        legend.setAlignmentX(Component.LEFT_ALIGNMENT);
        legend.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        JLabel legLivre = new JLabel("🟢 Livre — clique para selecionar");
        legLivre.setFont(new Font("Arial", Font.PLAIN, 11));
        legLivre.setForeground(new Color(76, 175, 80));
        JLabel legOcupada = new JLabel("🔴 Ocupada — indisponível");
        legOcupada.setFont(new Font("Arial", Font.PLAIN, 11));
        legOcupada.setForeground(new Color(244, 67, 54));
        legend.add(legLivre);
        legend.add(legOcupada);
        content.add(legend);

        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private void refreshMesaGrid() {
        if (mesaGridPanel == null) return;
        mesaGridPanel.removeAll();
        for (Mesa mesa : controller.getListaMesas()) {
            mesaGridPanel.add(createMesaBtn(mesa));
        }
        mesaGridPanel.revalidate();
        mesaGridPanel.repaint();
    }

    private JButton createMesaBtn(Mesa mesa) {
        boolean isLivre = mesa.getStatus() == StatusMesa.LIVRE;
        JButton btn = new JButton();
        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
        btn.setBackground(isLivre ? new Color(235, 247, 235) : new Color(255, 235, 230));
        btn.setBorder(BorderFactory.createLineBorder(
            isLivre ? new Color(160, 215, 160) : new Color(250, 168, 150), 2));
        btn.setFocusPainted(false);
        btn.setCursor(isLivre ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        btn.setPreferredSize(new Dimension(120, 110));
        btn.setEnabled(isLivre);

        JLabel numberLabel = new JLabel(String.format("MESA %d", mesa.getNumero()));
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 13));
        numberLabel.setForeground(new Color(51, 51, 51));

        JLabel statusLabel = new JLabel(isLivre ? "LIVRE" : "OCUPADA");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 10));
        statusLabel.setForeground(isLivre ? new Color(76, 175, 80) : new Color(244, 67, 54));

        btn.add(Box.createVerticalStrut(12));
        btn.add(numberLabel);
        btn.add(Box.createVerticalStrut(8));
        btn.add(statusLabel);

        if (isLivre) {
            btn.addActionListener(e -> confirmarMesa(mesa));
        }

        return btn;
    }

    private void confirmarMesa(Mesa mesa) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja sentar na Mesa " + mesa.getNumero() + "?",
            "Confirmar Mesa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.abrirMesa(mesa.getNumero(), cliente);
                mesaSelecionada = mesa;
                stopMesaRefreshTimer();
                rootCardLayout.show(getContentPane(), "main");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                refreshMesaGrid();
            }
        }
    }

    private void startMesaRefreshTimer() {
        mesaRefreshTimer = new Timer();
        mesaRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refreshMesaGrid());
            }
        }, 2000, 2000);
    }

    private void stopMesaRefreshTimer() {
        if (mesaRefreshTimer != null) {
            mesaRefreshTimer.cancel();
            mesaRefreshTimer = null;
        }
    }

    private JPanel buildMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 246, 250));
        root.add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        tabs.setBackground(new Color(245, 246, 250));
        tabs.addTab("  Minha Conta  ", buildContaPanel());
        tabs.addTab("  Cardápio  ", buildCardapioPanel());
        tabs.addTab("  Meu Pedido  ", buildCarrinhoPanel());
        root.add(tabs, BorderLayout.CENTER);
        return root;
    }

    // ── HEADER ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(18, 18, 24));
        h.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 20));
        h.setPreferredSize(new Dimension(0, 56));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 13));
        left.setOpaque(false);

        JLabel logo = new JLabel("🍖");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 14));
        logo.setOpaque(true);
        logo.setBackground(new Color(0, 122, 255));
        logo.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        JLabel title = new JLabel("Fogo na Chapa");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel sub = new JLabel("Área do Cliente");
        sub.setForeground(new Color(120, 128, 160));
        sub.setFont(new Font("Arial", Font.PLAIN, 12));

        left.add(logo); left.add(title); left.add(sub);
        h.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        right.setOpaque(false);

        carrinhoCountLbl = new JLabel("🛒 0 item(s)");
        carrinhoCountLbl.setForeground(new Color(180, 185, 210));
        carrinhoCountLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel userLbl = new JLabel("Olá, " + cliente.getNome().split(" ")[0]);
        userLbl.setForeground(new Color(200, 205, 220));
        userLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton sairBtn = miniBtn("Sair");
        sairBtn.addActionListener(e -> {
            // Não fecha a mesa ao sair — pedido permanece aberto para o gerente
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen(controller).setVisible(true));
        });

        right.add(carrinhoCountLbl); right.add(userLbl); right.add(sairBtn);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ── ABA: MINHA CONTA ─────────────────────────────────────────────────────
    private JPanel buildContaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 246, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 90));

        bonusValLbl = new JLabel(String.format("R$ %.2f", cliente.getBonus()));
        totalGastoValLbl = new JLabel(String.format("R$ %.2f", cliente.getTotalGasto()));
        pedidosValLbl = new JLabel(String.valueOf(cliente.getHistoricoPedidos().size()));

        statsRow.add(statCard("Bônus Disponível", bonusValLbl, new Color(76, 175, 80)));
        statsRow.add(statCard("Total Gasto", totalGastoValLbl, new Color(0, 122, 255)));
        statsRow.add(statCard("Pedidos Realizados", pedidosValLbl, new Color(150, 90, 220)));
        panel.add(statsRow, BorderLayout.NORTH);

        JPanel histWrap = new JPanel(new BorderLayout());
        histWrap.setOpaque(false);
        histWrap.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel histTitle = new JLabel("Histórico de Pedidos");
        histTitle.setFont(new Font("Arial", Font.BOLD, 15));
        histTitle.setForeground(new Color(22, 24, 35));
        histWrap.add(histTitle, BorderLayout.NORTH);

        String[] cols = {"#", "Mesa", "Itens", "Total", "Status"};
        histModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reloadHistTable();

        JTable table = new JTable(histModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(new Color(40, 44, 60));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(232, 235, 246));
        table.getTableHeader().setForeground(new Color(70, 78, 110));
        table.setGridColor(new Color(228, 232, 244));
        table.getColumnModel().getColumn(2).setPreferredWidth(280);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(218, 222, 238), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        histWrap.add(scroll, BorderLayout.CENTER);

        JLabel note = new JLabel("💡 Você acumula 10% de bônus sobre cada pedido pago — use no próximo!");
        note.setFont(new Font("Arial", Font.ITALIC, 11));
        note.setForeground(new Color(100, 110, 165));
        note.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        histWrap.add(note, BorderLayout.SOUTH);

        panel.add(histWrap, BorderLayout.CENTER);
        return panel;
    }

    // ── ABA: CARDÁPIO ────────────────────────────────────────────────────────
    private JPanel buildCardapioPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 246, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel pageTitle = new JLabel("Cardápio");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 18));
        pageTitle.setForeground(new Color(22, 24, 35));

        JLabel pageSub = new JLabel("Selecione os itens e adicione ao seu pedido");
        pageSub.setFont(new Font("Arial", Font.PLAIN, 12));
        pageSub.setForeground(new Color(110, 118, 145));

        JPanel hdr = new JPanel();
        hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(pageTitle); hdr.add(Box.createVerticalStrut(3)); hdr.add(pageSub);
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(hdr, BorderLayout.NORTH);

        JTabbedPane catTabs = new JTabbedPane();
        catTabs.setFont(new Font("Arial", Font.PLAIN, 12));
        catTabs.setBackground(new Color(245, 246, 250));

        JPanel comidaGrid = buildCategoryGrid("Comida");
        JPanel bebidaGrid = buildCategoryGrid("Bebida");

        catTabs.addTab("🍽  Pratos", new JScrollPane(comidaGrid));
        catTabs.addTab("🥤  Bebidas", new JScrollPane(bebidaGrid));

        panel.add(catTabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCategoryGrid(String categoria) {
        JPanel grid = new JPanel(new GridLayout(0, 2, 14, 14));
        grid.setBackground(new Color(245, 246, 250));
        grid.setBorder(BorderFactory.createEmptyBorder(16, 4, 16, 4));

        for (ItemCardapio item : controller.getListaCardapio()) {
            if (!item.getCategoria().equals(categoria)) continue;
            grid.add(buildItemCard(item));
        }
        return grid;
    }

    private JPanel buildItemCard(ItemCardapio item) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(220, 224, 238));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 0));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel nameLbl = new JLabel(item.getNome());
        nameLbl.setFont(new Font("Arial", Font.BOLD, 13));
        nameLbl.setForeground(new Color(22, 24, 35));

        String desc = item.getDescricaoCompleta();
        if (desc.length() > 55) desc = desc.substring(0, 52) + "…";
        JLabel descLbl = new JLabel("<html><div style='width:200px'>" + desc + "</div></html>");
        descLbl.setFont(new Font("Arial", Font.PLAIN, 10));
        descLbl.setForeground(new Color(110, 118, 148));

        JLabel priceLbl = new JLabel(String.format("R$ %.2f", item.getPreco()));
        priceLbl.setFont(new Font("Arial", Font.BOLD, 14));
        priceLbl.setForeground(new Color(0, 122, 255));

        info.add(nameLbl);
        info.add(Box.createVerticalStrut(3));
        info.add(descLbl);
        info.add(Box.createVerticalStrut(6));
        info.add(priceLbl);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        JSpinner qty = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        qty.setMaximumSize(new Dimension(62, 28));
        qty.setPreferredSize(new Dimension(62, 28));
        qty.setFont(new Font("Arial", Font.PLAIN, 11));

        JButton addBtn = new JButton("+ Adicionar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0, 100, 200) :
                            getModel().isRollover() ? new Color(0, 112, 240) : new Color(0, 122, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 11));
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        addBtn.addActionListener(e -> {
            int qtd = (Integer) qty.getValue();
            carrinho.merge(item, qtd, Integer::sum);
            refreshCarrinho();
            JOptionPane.showMessageDialog(this,
                qtd + "× " + item.getNome() + " adicionado(s) ao pedido!",
                "Item adicionado", JOptionPane.INFORMATION_MESSAGE);
        });

        actions.add(qty);
        actions.add(Box.createVerticalStrut(6));
        actions.add(addBtn);

        card.add(info, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }

    // ── ABA: MEU PEDIDO / CARRINHO ───────────────────────────────────────────
    private JPanel buildCarrinhoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 246, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JLabel pageTitle = new JLabel("Meu Pedido");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 18));
        pageTitle.setForeground(new Color(22, 24, 35));
        panel.add(pageTitle, BorderLayout.NORTH);

        // Center: split pane com itens já enviados + carrinho novo
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 246, 250));

        // ── Seção: Itens já enviados à cozinha ──────────────────────────────
        JLabel enviadosTitle = new JLabel("Itens já enviados à cozinha:");
        enviadosTitle.setFont(new Font("Arial", Font.BOLD, 13));
        enviadosTitle.setForeground(new Color(60, 100, 60));
        enviadosTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        enviadosTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        centerPanel.add(enviadosTitle);

        String[] envCols = {"Item", "Qtd", "Subtotal", "Status"};
        itensPedidoModel = new DefaultTableModel(envCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable enviadosTable = new JTable(itensPedidoModel);
        enviadosTable.setFont(new Font("Arial", Font.PLAIN, 12));
        enviadosTable.setForeground(new Color(40, 44, 60));
        enviadosTable.setRowHeight(26);
        enviadosTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        enviadosTable.getTableHeader().setBackground(new Color(220, 240, 220));
        enviadosTable.getTableHeader().setForeground(new Color(40, 80, 40));
        enviadosTable.setGridColor(new Color(200, 230, 200));
        enviadosTable.setBackground(new Color(248, 255, 248));
        JScrollPane envScroll = new JScrollPane(enviadosTable);
        envScroll.setBorder(BorderFactory.createLineBorder(new Color(180, 220, 180), 1));
        envScroll.getViewport().setBackground(new Color(248, 255, 248));
        envScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        envScroll.setPreferredSize(new Dimension(0, 130));
        envScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(envScroll);

        itensPedidoTotalLbl = new JLabel("Total enviado: R$ 0,00");
        itensPedidoTotalLbl.setFont(new Font("Arial", Font.ITALIC, 11));
        itensPedidoTotalLbl.setForeground(new Color(60, 130, 60));
        itensPedidoTotalLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        itensPedidoTotalLbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        centerPanel.add(itensPedidoTotalLbl);
        refreshItensPedido();

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(200, 210, 230));
        centerPanel.add(sep);
        centerPanel.add(Box.createVerticalStrut(10));

        // ── Seção: Carrinho (novos itens) ───────────────────────────────────
        JLabel carrinhoTitle = new JLabel("Adicionar mais itens:");
        carrinhoTitle.setFont(new Font("Arial", Font.BOLD, 13));
        carrinhoTitle.setForeground(new Color(22, 24, 35));
        carrinhoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        carrinhoTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        centerPanel.add(carrinhoTitle);

        String[] cols = {"Item", "Categoria", "Qtd", "Preço Unit.", "Subtotal"};
        carrinhoModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(carrinhoModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(new Color(40, 44, 60));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(232, 235, 246));
        table.getTableHeader().setForeground(new Color(70, 78, 110));
        table.setGridColor(new Color(228, 232, 244));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem removeItem = new JMenuItem("Remover item");
        removeItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                ArrayList<ItemCardapio> keys = new ArrayList<>(carrinho.keySet());
                if (row < keys.size()) {
                    carrinho.remove(keys.get(row));
                    refreshCarrinho();
                }
            }
        });
        popup.add(removeItem);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) table.setRowSelectionInterval(row, row);
                    popup.show(table, e.getX(), e.getY());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(218, 222, 238), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(scroll);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        carrinhoTotalLbl = new JLabel("Total novos itens: R$ 0,00");
        carrinhoTotalLbl.setFont(new Font("Arial", Font.BOLD, 16));
        carrinhoTotalLbl.setForeground(new Color(22, 24, 35));
        carrinhoTotalLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel hintLbl = new JLabel("Clique direito em um item para removê-lo");
        hintLbl.setFont(new Font("Arial", Font.ITALIC, 10));
        hintLbl.setForeground(new Color(140, 145, 170));
        hintLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton clearBtn = new JButton("Limpar carrinho");
        clearBtn.setBackground(new Color(230, 232, 242));
        clearBtn.setForeground(new Color(80, 85, 115));
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        clearBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            carrinho.clear();
            refreshCarrinho();
        });

        JButton payBtn = new JButton("Finalizar e Pagar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(50, 150, 60) :
                            getModel().isRollover() ? new Color(68, 188, 80) : new Color(76, 175, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Arial", Font.BOLD, 13));
        payBtn.setContentAreaFilled(false);
        payBtn.setBorderPainted(false);
        payBtn.setFocusPainted(false);
        payBtn.setBorder(BorderFactory.createEmptyBorder(9, 24, 9, 24));
        payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payBtn.addActionListener(e -> showPaymentDialog());

        btnRow.add(clearBtn); btnRow.add(payBtn);

        bottomPanel.add(hintLbl);
        bottomPanel.add(Box.createVerticalStrut(6));
        bottomPanel.add(carrinhoTotalLbl);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(btnRow);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── PAGAMENTO ─────────────────────────────────────────────────────────────
    private void showPaymentDialog() {
        // Verifica se há itens no carrinho OU no pedido já existente
        boolean temItensNoPedido = false;
        if (mesaSelecionada != null) {
            Pedido p = controller.buscarPedidoDaMesa(mesaSelecionada.getNumero());
            temItensNoPedido = p != null && !p.getItens().isEmpty();
        }
        if (carrinho.isEmpty() && !temItensNoPedido) {
            JOptionPane.showMessageDialog(this, "Adicione itens ao pedido antes de pagar.",
                    "Pedido vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Total = itens do pedido existente + novos itens do carrinho
        double totalPedidoExistente = mesaSelecionada != null ? controller.calcularTotalPedido(mesaSelecionada.getNumero()) : 0;
        double totalCarrinho = calcularTotalCarrinho();
        double total = totalPedidoExistente + totalCarrinho;

        JDialog dialog = new JDialog(this, "Finalizar Pedido", true);
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(this);

        JPanel cp = new JPanel();
        cp.setBackground(Color.WHITE);
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel ttl = new JLabel("Resumo do Pedido");
        ttl.setFont(new Font("Arial", Font.BOLD, 16));
        ttl.setForeground(new Color(22, 24, 35));
        ttl.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(ttl);
        cp.add(Box.createVerticalStrut(12));

        // Itens já no pedido
        if (temItensNoPedido) {
            Pedido p = controller.buscarPedidoDaMesa(mesaSelecionada.getNumero());
            for (ItemPedido ip : p.getItens()) {
                JLabel il = new JLabel(String.format("  %d× %s — R$ %.2f%s",
                        ip.getQuantidade(), ip.getItem().getNome(), ip.calcularSubtotal(),
                        ip.isEntregue() ? " ✅" : " ⏳"));
                il.setFont(new Font("Arial", Font.PLAIN, 12));
                il.setForeground(new Color(60, 65, 90));
                il.setAlignmentX(Component.LEFT_ALIGNMENT);
                cp.add(il);
            }
        }
        // Novos itens do carrinho
        for (Map.Entry<ItemCardapio, Integer> e : carrinho.entrySet()) {
            JLabel il = new JLabel(String.format("  %d× %s — R$ %.2f  [novo]",
                    e.getValue(), e.getKey().getNome(), e.getKey().getPreco() * e.getValue()));
            il.setFont(new Font("Arial", Font.PLAIN, 12));
            il.setForeground(new Color(0, 100, 180));
            il.setAlignmentX(Component.LEFT_ALIGNMENT);
            cp.add(il);
        }
        cp.add(Box.createVerticalStrut(10));

        JLabel totalLbl = new JLabel(String.format("Total: R$ %.2f", total));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 14));
        totalLbl.setForeground(new Color(22, 24, 35));
        totalLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(totalLbl);
        cp.add(Box.createVerticalStrut(16));

        JLabel metodolbl = new JLabel("Forma de pagamento:");
        metodolbl.setFont(new Font("Arial", Font.BOLD, 11));
        metodolbl.setForeground(new Color(60, 65, 80));
        metodolbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(metodolbl);
        cp.add(Box.createVerticalStrut(4));

        JComboBox<String> metodoCombo = new JComboBox<>(new String[]{
                "Cartão de Crédito", "Cartão de Débito", "Dinheiro", "PIX"});
        metodoCombo.setBackground(Color.WHITE);
        metodoCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        metodoCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        metodoCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(metodoCombo);
        cp.add(Box.createVerticalStrut(12));

        JCheckBox bonusCheck = new JCheckBox();
        double bonusDisp = cliente.getBonus();
        if (bonusDisp > 0) {
            double desc = Math.min(bonusDisp, total);
            bonusCheck.setText(String.format("Usar bônus (R$ %.2f disponível) → desconto de R$ %.2f", bonusDisp, desc));
            bonusCheck.setFont(new Font("Arial", Font.PLAIN, 11));
            bonusCheck.setBackground(Color.WHITE);
            bonusCheck.setForeground(new Color(60, 150, 70));
            bonusCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
            cp.add(bonusCheck);
            cp.add(Box.createVerticalStrut(8));
        }

        JLabel bonusEarn = new JLabel("Você acumulará 10% do valor pago em bônus!");
        bonusEarn.setFont(new Font("Arial", Font.ITALIC, 10));
        bonusEarn.setForeground(new Color(100, 110, 200));
        bonusEarn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(bonusEarn);
        cp.add(Box.createVerticalGlue());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(228, 230, 242));
        cancelBtn.setForeground(new Color(60, 65, 90));
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton confirmBtn = new JButton("Confirmar Pagamento") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(50, 150, 60) : new Color(76, 175, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.setContentAreaFilled(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        confirmBtn.addActionListener(e -> {
            if (mesaSelecionada == null) {
                JOptionPane.showMessageDialog(dialog, "Nenhuma mesa selecionada.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int numMesa = mesaSelecionada.getNumero();
                // Adicionar novos itens do carrinho ao pedido já aberto na mesa
                for (Map.Entry<ItemCardapio, Integer> entry : carrinho.entrySet()) {
                    controller.adicionarItemAoPedido(numMesa, entry.getKey(), entry.getValue());
                }
                String metodo = (String) metodoCombo.getSelectedItem();
                boolean usarBonus = bonusCheck.isSelected();
                controller.efetuarPagamento(numMesa, metodo, usarBonus);

                carrinho.clear();
                mesaSelecionada = null;
                refreshCarrinho();
                refreshItensPedido();
                refreshStats();
                reloadHistTable();

                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                        String.format("Pagamento realizado com sucesso!\n\nBônus atual: R$ %.2f", cliente.getBonus()),
                        "Pedido confirmado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao processar: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn); btnRow.add(confirmBtn);
        cp.add(btnRow);

        dialog.add(cp);
        dialog.setVisible(true);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private void refreshCarrinho() {
        if (carrinhoModel == null) return;
        carrinhoModel.setRowCount(0);
        double total = 0;
        for (Map.Entry<ItemCardapio, Integer> e : carrinho.entrySet()) {
            double sub = e.getKey().getPreco() * e.getValue();
            total += sub;
            carrinhoModel.addRow(new Object[]{
                e.getKey().getNome(),
                e.getKey().getCategoria(),
                e.getValue(),
                String.format("R$ %.2f", e.getKey().getPreco()),
                String.format("R$ %.2f", sub)
            });
        }
        int totalItens = carrinho.values().stream().mapToInt(i -> i).sum();
        if (carrinhoCountLbl != null) carrinhoCountLbl.setText("🛒 " + totalItens + " item(s)");
        if (carrinhoTotalLbl != null) carrinhoTotalLbl.setText(String.format("Total novos itens: R$ %.2f", total));
    }

    private void refreshItensPedido() {
        if (itensPedidoModel == null) return;
        itensPedidoModel.setRowCount(0);
        double total = 0;
        if (mesaSelecionada != null) {
            Pedido pedido = controller.buscarPedidoDaMesa(mesaSelecionada.getNumero());
            if (pedido != null) {
                for (ItemPedido ip : pedido.getItens()) {
                    double sub = ip.calcularSubtotal();
                    total += sub;
                    itensPedidoModel.addRow(new Object[]{
                        ip.getItem().getNome(),
                        ip.getQuantidade(),
                        String.format("R$ %.2f", sub),
                        ip.isEntregue() ? "✅ Entregue" : "⏳ Aguardando"
                    });
                }
            }
        }
        if (itensPedidoTotalLbl != null)
            itensPedidoTotalLbl.setText(total > 0 ? String.format("Total enviado: R$ %.2f", total) : "Nenhum item enviado ainda.");
    }

    private double calcularTotalCarrinho() {
        double t = 0;
        for (Map.Entry<ItemCardapio, Integer> e : carrinho.entrySet())
            t += e.getKey().getPreco() * e.getValue();
        return t;
    }

    private void refreshStats() {
        if (bonusValLbl != null) bonusValLbl.setText(String.format("R$ %.2f", cliente.getBonus()));
        if (totalGastoValLbl != null) totalGastoValLbl.setText(String.format("R$ %.2f", cliente.getTotalGasto()));
        if (pedidosValLbl != null) pedidosValLbl.setText(String.valueOf(cliente.getHistoricoPedidos().size()));
    }

    private void reloadHistTable() {
        if (histModel == null) return;
        histModel.setRowCount(0);
        for (int i = 0; i < cliente.getHistoricoPedidos().size(); i++) {
            Pedido p = cliente.getHistoricoPedidos().get(i);
            StringBuilder itens = new StringBuilder();
            for (ItemPedido ip : p.getItens()) {
                if (itens.length() > 0) itens.append(", ");
                itens.append(ip.getQuantidade()).append("× ").append(ip.getItem().getNome());
            }
            histModel.addRow(new Object[]{
                "#" + (i + 1),
                p.getMesa() != null ? "Mesa " + p.getMesa().getNumero() : "—",
                itens.length() == 0 ? "—" : itens.toString(),
                String.format("R$ %.2f", p.calcularTotal()),
                p.getStatus()
            });
        }
    }

    private JPanel statCard(String label, JLabel valLbl, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(218, 222, 238));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        valLbl.setFont(new Font("Arial", Font.BOLD, 20));
        valLbl.setForeground(accent);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(new Color(110, 115, 145));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(Box.createVerticalStrut(4));
        card.add(valLbl);
        card.add(Box.createVerticalStrut(3));
        card.add(lbl);
        return card;
    }

    private JButton miniBtn(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(50, 52, 70));
        btn.setForeground(new Color(200, 205, 220));
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    public void dispose() {
        stopMesaRefreshTimer();
        super.dispose();
    }
}
