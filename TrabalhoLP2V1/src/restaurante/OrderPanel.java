package restaurante;

import model.ItemCardapio;
import model.Pedido;
import model.ItemPedido;
import model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class OrderPanel extends JPanel {
    private RestauranteController controller;
    private JFrame parentFrame;

    // Tab: Pedidos finalizados
    private JTable pedidosTable;
    private DefaultTableModel pedidosModel;

    // Tab: Cozinha (pedidos ativos/pendentes)
    private JTable cozinhaTable;
    private DefaultTableModel cozinhaModel;
    private JPanel cozinhaCardsContainer; // direct ref for refresh

    public OrderPanel(RestauranteController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.setBackground(new Color(245, 245, 245));
        tabs.setForeground(new Color(40, 44, 60));

        tabs.addTab("  📋 Pedidos  ", buildPedidosPanel());
        tabs.addTab("  🍳 Cozinha  ", buildCozinhaPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // ── ABA PEDIDOS (finalizados) ─────────────────────────────────────────────
    private JPanel buildPedidosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 245, 245));
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 12, 28));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Histórico de Pedidos");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(22, 24, 35));

        JLabel sub = new JLabel("Pedidos finalizados e pagos");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(110, 118, 145));

        titles.add(title);
        titles.add(Box.createVerticalStrut(3));
        titles.add(sub);
        header.add(titles, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"#", "Mesa", "Cliente", "Itens", "Total (R$)", "Status"};
        pedidosModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        pedidosTable = new JTable(pedidosModel);
        styleTable(pedidosTable);
        pedidosTable.getColumnModel().getColumn(0).setMaxWidth(50);
        pedidosTable.getColumnModel().getColumn(1).setMaxWidth(80);
        pedidosTable.getColumnModel().getColumn(4).setMaxWidth(110);
        pedidosTable.getColumnModel().getColumn(5).setMaxWidth(90);
        pedidosTable.getColumnModel().getColumn(3).setPreferredWidth(320);

        // Color PAGO rows green
        pedidosTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (sel) {
                    comp.setBackground(new Color(0, 100, 210));
                    comp.setForeground(Color.WHITE);
                } else {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 249, 253));
                    comp.setForeground(new Color(40, 44, 60));
                }
                return comp;
            }
        });

        refreshPedidosTable();

        JScrollPane scroll = new JScrollPane(pedidosTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(218, 222, 238), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        footer.setBackground(new Color(245, 245, 245));
        JLabel hint = new JLabel("📌 Pedidos são registrados aqui após o pagamento");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(140, 145, 170));
        footer.add(hint);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // ── ABA COZINHA (pedidos ativos/pendentes) ────────────────────────────────
    private JPanel buildCozinhaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 245, 245));
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 12, 28));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Visão da Cozinha — Pedidos Pendentes");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(22, 24, 35));

        JLabel sub = new JLabel("Itens aguardando preparo ou entrega");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(110, 118, 145));

        titles.add(title);
        titles.add(Box.createVerticalStrut(3));
        titles.add(sub);
        header.add(titles, BorderLayout.WEST);

        // Botão Adicionar Item + Atualizar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setBackground(new Color(245, 245, 245));

        JButton addBtn = styledBtn("+ Adicionar Item a Mesa", new Color(0, 122, 255));
        addBtn.addActionListener(e -> showAddItemModal());

        JButton refreshBtn = styledBtn("↻ Atualizar", new Color(80, 160, 80));
        refreshBtn.addActionListener(e -> refreshCozinhaCards());

        btnBar.add(addBtn);
        btnBar.add(refreshBtn);
        header.add(btnBar, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        // Cards scroll area
        JPanel cardsContent = new JPanel();
        cardsContent.setLayout(new BoxLayout(cardsContent, BoxLayout.Y_AXIS));
        cardsContent.setBackground(new Color(245, 245, 245));
        cardsContent.setName("cozinhaCards");
        cozinhaCardsContainer = cardsContent;

        JScrollPane scroll = new JScrollPane(cardsContent);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(245, 245, 245));
        panel.add(scroll, BorderLayout.CENTER);

        buildCozinhaCards(cardsContent);
        return panel;
    }

    private void buildCozinhaCards(JPanel container) {
        container.removeAll();
        container.setBorder(BorderFactory.createEmptyBorder(8, 28, 20, 28));

        if (controller.getListaPedidosAtivos().isEmpty()) {
            JLabel empty = new JLabel("Nenhum pedido pendente no momento.");
            empty.setFont(new Font("Arial", Font.ITALIC, 13));
            empty.setForeground(new Color(160, 165, 190));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            container.add(Box.createVerticalStrut(20));
            container.add(empty);
        } else {
            for (Pedido pedido : controller.getListaPedidosAtivos()) {
                container.add(buildKitchenCard(pedido, container));
                container.add(Box.createVerticalStrut(14));
            }
        }
        container.revalidate();
        container.repaint();
    }

    private void refreshCozinhaCards() {
        if (cozinhaCardsContainer != null) buildCozinhaCards(cozinhaCardsContainer);
    }

    private JPanel buildKitchenCard(Pedido pedido, JPanel container) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 235), 1),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card header
        String clienteNome = pedido.getCliente() != null ? pedido.getCliente().getNome() : "Sem cliente";
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setBackground(Color.WHITE);

        JLabel headerLbl = new JLabel(String.format("🍽  Mesa %d  —  %s  —  %s",
            pedido.getMesa().getNumero(), clienteNome, pedido.obterTempoFormatado()));
        headerLbl.setFont(new Font("Arial", Font.BOLD, 13));
        headerLbl.setForeground(new Color(22, 24, 35));
        cardHeader.add(headerLbl, BorderLayout.WEST);

        // Status badge
        JLabel statusBadge = new JLabel("  " + pedido.getStatus() + "  ");
        statusBadge.setFont(new Font("Arial", Font.BOLD, 10));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setOpaque(true);
        Color badgeColor = pedido.getStatus().equals("ENTREGUE") ? new Color(76, 175, 80) :
                           pedido.getStatus().equals("ABERTO") ? new Color(255, 152, 0) :
                           new Color(0, 122, 255);
        statusBadge.setBackground(badgeColor);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        cardHeader.add(statusBadge, BorderLayout.EAST);

        card.add(cardHeader);
        card.add(Box.createVerticalStrut(10));

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(230, 233, 245));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(10));

        // Items
        for (ItemPedido ip : pedido.getItens()) {
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(Color.WHITE);
            itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

            String icon = ip.isEntregue() ? "✅" : "⏳";
            JLabel itemLbl = new JLabel(String.format("%s  %dx %s  [%s]  — R$ %.2f",
                icon, ip.getQuantidade(), ip.getItem().getNome(),
                ip.getItem().getCategoria(), ip.calcularSubtotal()));
            itemLbl.setFont(new Font("Arial", ip.isEntregue() ? Font.ITALIC : Font.PLAIN, 12));
            itemLbl.setForeground(ip.isEntregue() ? new Color(140, 175, 140) : new Color(40, 44, 60));
            itemRow.add(itemLbl, BorderLayout.WEST);

            if (!ip.isEntregue()) {
                JButton entregarBtn = styledBtn("Entregar", new Color(76, 175, 80));
                entregarBtn.setFont(new Font("Arial", Font.BOLD, 10));
                entregarBtn.setPreferredSize(new Dimension(90, 26));
                entregarBtn.addActionListener(e -> {
                    controller.marcarItemComoEntregue(pedido, ip);
                    buildCozinhaCards(container);
                });
                itemRow.add(entregarBtn, BorderLayout.EAST);
            }
            card.add(itemRow);
        }

        card.add(Box.createVerticalStrut(10));

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);

        JLabel totalLbl = new JLabel(String.format("Total: R$ %.2f", pedido.calcularTotal()));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 13));
        totalLbl.setForeground(new Color(22, 24, 35));
        footer.add(totalLbl, BorderLayout.WEST);

        JPanel footerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footerBtns.setBackground(Color.WHITE);

        if (!pedido.getItens().isEmpty() && !pedido.todosItensEntregues()) {
            JButton entregarTudoBtn = styledBtn("Entregar Tudo", new Color(0, 122, 255));
            entregarTudoBtn.addActionListener(e -> {
                controller.entregarPedido(pedido.getMesa().getNumero());
                buildCozinhaCards(container);
                JOptionPane.showMessageDialog(parentFrame,
                    "Pedido da Mesa " + pedido.getMesa().getNumero() + " entregue!");
            });
            footerBtns.add(entregarTudoBtn);
        }

        JButton pagarBtn = styledBtn("💳 Pagar e Fechar Mesa", new Color(220, 100, 0));
        pagarBtn.addActionListener(e -> {
            showPaymentModal(pedido.getMesa().getNumero());
            buildCozinhaCards(container);
        });
        footerBtns.add(pagarBtn);

        footer.add(footerBtns, BorderLayout.EAST);
        card.add(footer);

        return card;
    }

    // ── TABLE REFRESH ─────────────────────────────────────────────────────────
    private void refreshPedidosTable() {
        pedidosModel.setRowCount(0);
        java.util.List<Pedido> finalizados = controller.getListaPedidosFinalizados();
        for (int i = 0; i < finalizados.size(); i++) {
            Pedido p = finalizados.get(i);
            StringBuilder itens = new StringBuilder();
            for (ItemPedido ip : p.getItens()) {
                if (itens.length() > 0) itens.append(", ");
                itens.append(ip.getQuantidade()).append("× ").append(ip.getItem().getNome());
            }
            String clienteNome = p.getCliente() != null ? p.getCliente().getNome() : "—";
            int numMesa = p.getMesa() != null ? p.getMesa().getNumero() : 0;
            pedidosModel.addRow(new Object[]{
                "#" + (i + 1),
                "Mesa " + numMesa,
                clienteNome,
                itens.length() == 0 ? "—" : itens.toString(),
                String.format("R$ %.2f", p.calcularTotal()),
                p.getStatus()
            });
        }
    }

    // ── ADD ITEM MODAL ────────────────────────────────────────────────────────
    private void showAddItemModal() {
        if (controller.getListaPedidosAtivos().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Nenhuma mesa com conta aberta!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (controller.getListaCardapio().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Cardápio vazio!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog modal = new JDialog((JFrame) parentFrame, "Adicionar Item a Mesa", true);
        modal.setSize(460, 300);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        cp.setBackground(Color.WHITE);

        cp.add(modalLabel("Mesa:"));
        JComboBox<String> mesaCombo = new JComboBox<>();
        for (Pedido p : controller.getListaPedidosAtivos())
            mesaCombo.addItem("Mesa " + p.getMesa().getNumero() + " — " +
                (p.getCliente() != null ? p.getCliente().getNome() : "Sem cliente"));
        styleCombo(mesaCombo); cp.add(mesaCombo); cp.add(Box.createVerticalStrut(10));

        cp.add(modalLabel("Item do Cardápio:"));
        JComboBox<String> itemCombo = new JComboBox<>();
        for (ItemCardapio item : controller.getListaCardapio())
            itemCombo.addItem("[" + item.getCategoria() + "] " + item.getNome() + " — R$ " +
                String.format("%.2f", item.getPreco()));
        styleCombo(itemCombo); cp.add(itemCombo); cp.add(Box.createVerticalStrut(10));

        cp.add(modalLabel("Quantidade:"));
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        qtySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cp.add(qtySpinner); cp.add(Box.createVerticalStrut(18));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        JButton cancelBtn = styledBtn("Cancelar", new Color(140, 145, 165));
        cancelBtn.addActionListener(e -> modal.dispose());
        JButton confirmBtn = styledBtn("Adicionar ao Pedido", new Color(0, 122, 255));
        confirmBtn.addActionListener(e -> {
            try {
                int mesaIdx = mesaCombo.getSelectedIndex();
                int itemIdx = itemCombo.getSelectedIndex();
                int qtd = (Integer) qtySpinner.getValue();
                Pedido pedido = controller.getListaPedidosAtivos().get(mesaIdx);
                ItemCardapio item = controller.getListaCardapio().get(itemIdx);
                controller.adicionarItemAoPedido(pedido.getMesa().getNumero(), item, qtd);
                refreshCozinhaCards();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Item adicionado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(cancelBtn); btnPanel.add(confirmBtn);
        cp.add(btnPanel);
        modal.add(cp);
        modal.setVisible(true);
    }

    // ── PAYMENT MODAL ─────────────────────────────────────────────────────────
    private void showPaymentModal(int numeroMesa) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Pagamento — Mesa " + numeroMesa, true);
        modal.setSize(460, 370);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        cp.setBackground(Color.WHITE);

        double total = controller.calcularTotalPedido(numeroMesa);
        Cliente cliente = controller.obterClienteDaMesa(numeroMesa);

        JLabel titleLbl = new JLabel("Pagamento — Mesa " + numeroMesa);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
        titleLbl.setForeground(new Color(22, 24, 35));
        cp.add(titleLbl); cp.add(Box.createVerticalStrut(12));

        if (cliente != null) {
            JLabel cLbl = new JLabel("Cliente: " + cliente.getNome());
            cLbl.setFont(new Font("Arial", Font.PLAIN, 12));
            cLbl.setForeground(new Color(80, 85, 110));
            cp.add(cLbl);
        }

        JLabel totalLbl = new JLabel(String.format("Total: R$ %.2f", total));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 14));
        totalLbl.setForeground(new Color(22, 24, 35));
        cp.add(totalLbl); cp.add(Box.createVerticalStrut(14));

        cp.add(modalLabel("Forma de Pagamento:"));
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Cartão de Crédito", "Cartão de Débito", "Dinheiro", "PIX"});
        styleCombo(methodCombo); cp.add(methodCombo); cp.add(Box.createVerticalStrut(12));

        JCheckBox useBonus = new JCheckBox();
        if (cliente != null && cliente.getBonus() > 0) {
            double desc = Math.min(cliente.getBonus(), total);
            useBonus.setText(String.format("Usar bônus (R$ %.2f) → Total: R$ %.2f", cliente.getBonus(), total - desc));
            useBonus.setFont(new Font("Arial", Font.PLAIN, 11));
            useBonus.setBackground(Color.WHITE);
            useBonus.setForeground(new Color(60, 150, 70));
            cp.add(useBonus); cp.add(Box.createVerticalStrut(6));
        }

        if (cliente != null) {
            JLabel bonusInfo = new JLabel("Você receberá 10% do valor pago em bônus!");
            bonusInfo.setFont(new Font("Arial", Font.ITALIC, 10));
            bonusInfo.setForeground(new Color(100, 110, 200));
            cp.add(bonusInfo);
        }

        cp.add(Box.createVerticalGlue());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        JButton cancelBtn = styledBtn("Cancelar", new Color(140, 145, 165));
        cancelBtn.addActionListener(e -> modal.dispose());
        JButton confirmBtn = styledBtn("Confirmar Pagamento", new Color(76, 175, 80));
        confirmBtn.addActionListener(e -> {
            try {
                controller.efetuarPagamento(numeroMesa, (String) methodCombo.getSelectedItem(), useBonus.isSelected());
                refresh();
                if (parentFrame instanceof MainFrame) ((MainFrame) parentFrame).refreshSidebar();
                modal.dispose();
                String msg = "Pagamento realizado! Mesa liberada.";
                if (cliente != null) msg += String.format("\nBônus atual: R$ %.2f", cliente.getBonus());
                JOptionPane.showMessageDialog(parentFrame, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(cancelBtn); btnPanel.add(confirmBtn);
        cp.add(btnPanel);
        modal.add(cp);
        modal.setVisible(true);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(new Color(40, 44, 60));
        table.setRowHeight(30);
        table.setGridColor(new Color(228, 232, 244));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(232, 235, 246));
        table.getTableHeader().setForeground(new Color(60, 65, 100));
        table.getTableHeader().setReorderingAllowed(false);
    }

    private JButton styledBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        return btn;
    }

    private JLabel modalLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(new Color(60, 65, 80));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(Color.WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public void refresh() {
        refreshPedidosTable();
        refreshCozinhaCards();
    }
}
