package restaurante;

import model.ItemCardapio;
import model.Pedido;
import model.ItemPedido;
import model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class OrderPanel extends JPanel {
    private RestauranteController controller;
    private JFrame parentFrame;
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public OrderPanel(RestauranteController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        initComponents();
    }

    private void initComponents() {
        // Tabs: Pedidos Ativos | Visão da Cozinha
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));

        tabs.addTab("📋 Pedidos Ativos", buildPedidosPanel());
        tabs.addTab("🍳 Cozinha", buildCozinhaPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildPedidosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 16, 32));

        JLabel titleLabel = new JLabel("Monitor de Contas e Pedidos Ativos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel countLabel = new JLabel(
            controller.getListaPedidosAtivos().size() + " mesa(s) com conta aberta"
        );
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        countLabel.setForeground(new Color(68, 68, 68));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(245, 245, 245));
        textPanel.add(titleLabel);
        textPanel.add(countLabel);

        JButton addBtn = new JButton("+ Adicionar Item a Mesa");
        addBtn.setBackground(new Color(0, 122, 255));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.addActionListener(e -> showAddItemModal());

        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"Mesa", "Cliente", "Status", "Itens", "Total", "Ação"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ordersTable.getTableHeader().setBackground(new Color(240, 240, 240));
        ordersTable.getTableHeader().setForeground(new Color(51, 51, 51));
        ordersTable.setForeground(new Color(51, 51, 51));
        ordersTable.setRowHeight(36);
        ordersTable.getColumnModel().getColumn(5).setMaxWidth(80);

        ordersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = ordersTable.getSelectedRow();
                int col = ordersTable.getSelectedColumn();
                if (row >= 0 && col == 5 && row < controller.getListaPedidosAtivos().size()) {
                    Pedido p = controller.getListaPedidosAtivos().get(row);
                    showPaymentModal(p.getMesa().getNumero());
                }
            }
        });

        refreshTable();

        JScrollPane sp = new JScrollPane(ordersTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(232, 232, 232)));
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildCozinhaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 16, 32));

        JLabel titleLabel = new JLabel("Visão da Cozinha — Pedidos em Aberto");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        JButton refreshBtn = new JButton("↻ Atualizar");
        refreshBtn.setBackground(new Color(80, 180, 80));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel kitchenScrollContent = new JPanel();
        kitchenScrollContent.setLayout(new BoxLayout(kitchenScrollContent, BoxLayout.Y_AXIS));
        kitchenScrollContent.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(kitchenScrollContent);
        scrollPane.setBackground(new Color(245, 245, 245));
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        Runnable buildCards = () -> {
            kitchenScrollContent.removeAll();
            kitchenScrollContent.setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));

            if (controller.getListaPedidosAtivos().isEmpty()) {
                JLabel empty = new JLabel("Nenhum pedido em aberto no momento.");
                empty.setFont(new Font("Arial", Font.ITALIC, 13));
                empty.setForeground(new Color(150, 150, 150));
                empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                kitchenScrollContent.add(empty);
            } else {
                for (Pedido pedido : controller.getListaPedidosAtivos()) {
                    kitchenScrollContent.add(buildKitchenCard(pedido));
                    kitchenScrollContent.add(Box.createVerticalStrut(16));
                }
            }
            kitchenScrollContent.revalidate();
            kitchenScrollContent.repaint();
        };

        buildCards.run();
        refreshBtn.addActionListener(e -> buildCards.run());

        return panel;
    }

    private JPanel buildKitchenCard(Pedido pedido) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        String clienteNome = pedido.getCliente() != null ? pedido.getCliente().getNome() : "Sem cliente";
        JLabel headerLbl = new JLabel(String.format("🍽 Mesa %d — %s — %s — %s",
            pedido.getMesa().getNumero(), clienteNome, pedido.getStatus(), pedido.obterTempoFormatado()));
        headerLbl.setFont(new Font("Arial", Font.BOLD, 13));
        headerLbl.setForeground(new Color(51, 51, 51));
        headerLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(headerLbl);
        card.add(Box.createVerticalStrut(8));

        for (ItemPedido ip : pedido.getItens()) {
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(Color.WHITE);
            itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            Color itemColor = ip.isEntregue() ? new Color(150, 200, 150) : new Color(51, 51, 51);
            String entregueIcon = ip.isEntregue() ? "✅ " : "⏳ ";
            JLabel itemLbl = new JLabel(entregueIcon + ip.getQuantidade() + "× " + ip.getItem().getNome()
                + "  [" + ip.getItem().getCategoria() + "]");
            itemLbl.setFont(new Font("Arial", ip.isEntregue() ? Font.ITALIC : Font.PLAIN, 12));
            itemLbl.setForeground(itemColor);

            if (!ip.isEntregue()) {
                JButton entregarBtn = new JButton("Entregar");
                entregarBtn.setBackground(new Color(76, 175, 80));
                entregarBtn.setForeground(Color.WHITE);
                entregarBtn.setFont(new Font("Arial", Font.BOLD, 10));
                entregarBtn.setPreferredSize(new Dimension(90, 24));
                entregarBtn.addActionListener(e -> {
                    controller.marcarItemComoEntregue(pedido, ip);
                    // Rebuild cards
                    JComponent parent = (JComponent) card.getParent();
                    if (parent != null) {
                        parent.removeAll();
                        parent.setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));
                        for (Pedido p2 : controller.getListaPedidosAtivos()) {
                            parent.add(buildKitchenCard(p2));
                            parent.add(Box.createVerticalStrut(16));
                        }
                        parent.revalidate();
                        parent.repaint();
                    }
                });
                itemRow.add(entregarBtn, BorderLayout.EAST);
            }

            itemRow.add(itemLbl, BorderLayout.WEST);
            card.add(itemRow);
        }

        card.add(Box.createVerticalStrut(6));

        JPanel footerRow = new JPanel(new BorderLayout());
        footerRow.setBackground(Color.WHITE);

        JLabel totalLbl = new JLabel(String.format("Total: R$ %.2f", pedido.calcularTotal()));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 12));
        totalLbl.setForeground(new Color(51, 51, 51));
        footerRow.add(totalLbl, BorderLayout.WEST);

        if (!pedido.getItens().isEmpty() && !pedido.todosItensEntregues()) {
            JButton entregarTudoBtn = new JButton("Entregar Tudo");
            entregarTudoBtn.setBackground(new Color(0, 122, 255));
            entregarTudoBtn.setForeground(Color.WHITE);
            entregarTudoBtn.setFont(new Font("Arial", Font.BOLD, 10));
            entregarTudoBtn.addActionListener(e -> {
                controller.entregarPedido(pedido.getMesa().getNumero());
                JComponent parent = (JComponent) card.getParent();
                if (parent != null) {
                    parent.removeAll();
                    parent.setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));
                    if (controller.getListaPedidosAtivos().isEmpty()) {
                        JLabel empty = new JLabel("Nenhum pedido em aberto no momento.");
                        empty.setFont(new Font("Arial", Font.ITALIC, 13));
                        empty.setForeground(new Color(150,150,150));
                        parent.add(empty);
                    } else {
                        for (Pedido p2 : controller.getListaPedidosAtivos()) {
                            parent.add(buildKitchenCard(p2));
                            parent.add(Box.createVerticalStrut(16));
                        }
                    }
                    parent.revalidate();
                    parent.repaint();
                }
                JOptionPane.showMessageDialog(parentFrame, "Pedido da Mesa " + pedido.getMesa().getNumero() + " entregue com sucesso!");
            });
            footerRow.add(entregarTudoBtn, BorderLayout.EAST);
        }

        card.add(footerRow);
        return card;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("0.00");

        for (Pedido pedido : controller.getListaPedidosAtivos()) {
            String mesa = "MESA " + pedido.getMesa().getNumero();
            String cliente = pedido.getCliente() != null ? pedido.getCliente().getNome() : "—";

            StringBuilder itens = new StringBuilder();
            for (ItemPedido ip : pedido.getItens()) {
                if (itens.length() > 0) itens.append(", ");
                itens.append(ip.getQuantidade()).append("× ").append(ip.getItem().getNome());
            }

            String total = "R$ " + df.format(pedido.calcularTotal()).replace(".", ",");
            tableModel.addRow(new Object[]{mesa, cliente, pedido.getStatus(), itens.toString(), total, "💳 Pagar"});
        }
    }

    private void showAddItemModal() {
        if (controller.getListaPedidosAtivos().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Nenhuma mesa com conta aberta!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (controller.getListaCardapio().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Cardápio vazio! Adicione itens primeiro.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog modal = new JDialog((JFrame) parentFrame, "Adicionar Item a Mesa", true);
        modal.setSize(450, 320);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cp.setBackground(Color.WHITE);

        JLabel mesaLbl = new JLabel("Mesa:");
        mesaLbl.setFont(new Font("Arial", Font.BOLD, 11)); mesaLbl.setForeground(new Color(51,51,51));
        cp.add(mesaLbl);
        JComboBox<String> mesaCombo = new JComboBox<>();
        for (Pedido p : controller.getListaPedidosAtivos())
            mesaCombo.addItem("Mesa " + p.getMesa().getNumero() + " — " + (p.getCliente() != null ? p.getCliente().getNome() : "Sem cliente"));
        mesaCombo.setBackground(Color.WHITE); mesaCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        mesaCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cp.add(mesaCombo);
        cp.add(Box.createVerticalStrut(12));

        JLabel itemLbl = new JLabel("Item do Cardápio:");
        itemLbl.setFont(new Font("Arial", Font.BOLD, 11)); itemLbl.setForeground(new Color(51,51,51));
        cp.add(itemLbl);
        JComboBox<String> itemCombo = new JComboBox<>();
        for (ItemCardapio item : controller.getListaCardapio())
            itemCombo.addItem("[" + item.getCategoria() + "] " + item.getNome() + " — R$ " + String.format("%.2f", item.getPreco()));
        itemCombo.setBackground(Color.WHITE); itemCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        itemCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cp.add(itemCombo);
        cp.add(Box.createVerticalStrut(12));

        JLabel qtyLbl = new JLabel("Quantidade:");
        qtyLbl.setFont(new Font("Arial", Font.BOLD, 11)); qtyLbl.setForeground(new Color(51,51,51));
        cp.add(qtyLbl);
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        qtySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cp.add(qtySpinner);
        cp.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230,230,230)); cancelBtn.setForeground(new Color(51,51,51));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton confirmBtn = new JButton("Adicionar ao Pedido");
        confirmBtn.setBackground(new Color(0,122,255)); confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.addActionListener(e -> {
            try {
                int mesaIdx = mesaCombo.getSelectedIndex();
                int itemIdx = itemCombo.getSelectedIndex();
                int qtd = (Integer) qtySpinner.getValue();
                Pedido pedido = controller.getListaPedidosAtivos().get(mesaIdx);
                ItemCardapio item = controller.getListaCardapio().get(itemIdx);
                controller.adicionarItemAoPedido(pedido.getMesa().getNumero(), item, qtd);
                refreshTable();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Item adicionado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        cp.add(btnPanel);
        modal.add(cp);
        modal.setVisible(true);
    }

    private void showPaymentModal(int numeroMesa) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Pagamento — Mesa " + numeroMesa, true);
        modal.setSize(450, 360);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cp.setBackground(Color.WHITE);

        double total = controller.calcularTotalPedido(numeroMesa);
        Cliente cliente = controller.obterClienteDaMesa(numeroMesa);

        JLabel titleLbl = new JLabel("Pagamento — Mesa " + numeroMesa);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 14)); titleLbl.setForeground(new Color(51,51,51));
        cp.add(titleLbl);
        cp.add(Box.createVerticalStrut(10));

        if (cliente != null) {
            JLabel cLbl = new JLabel("Cliente: " + cliente.getNome());
            cLbl.setFont(new Font("Arial", Font.PLAIN, 11)); cLbl.setForeground(new Color(80,80,80));
            cp.add(cLbl);
        }

        JLabel totalLbl = new JLabel(String.format("Total: R$ %.2f", total));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 13)); totalLbl.setForeground(new Color(51,51,51));
        cp.add(totalLbl);
        cp.add(Box.createVerticalStrut(12));

        JLabel methodLbl = new JLabel("Forma de Pagamento:");
        methodLbl.setFont(new Font("Arial", Font.BOLD, 11)); methodLbl.setForeground(new Color(51,51,51));
        cp.add(methodLbl);
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Cartão de Crédito", "Cartão de Débito", "Dinheiro", "PIX"});
        methodCombo.setBackground(Color.WHITE); methodCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cp.add(methodCombo);
        cp.add(Box.createVerticalStrut(12));

        JCheckBox useBonus = new JCheckBox();
        if (cliente != null && cliente.getBonus() > 0) {
            double desc = Math.min(cliente.getBonus(), total);
            useBonus.setText(String.format("Usar bônus acumulado (R$ %.2f) → Total final: R$ %.2f", cliente.getBonus(), total - desc));
            useBonus.setFont(new Font("Arial", Font.PLAIN, 11));
            useBonus.setBackground(Color.WHITE);
            cp.add(useBonus);
            cp.add(Box.createVerticalStrut(6));
        }

        if (cliente != null) {
            JLabel bonusInfo = new JLabel("Você receberá 10% do valor pago em bônus!");
            bonusInfo.setFont(new Font("Arial", Font.ITALIC, 10));
            bonusInfo.setForeground(new Color(100,100,200));
            cp.add(bonusInfo);
        }

        cp.add(Box.createVerticalGlue());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230,230,230)); cancelBtn.setForeground(new Color(51,51,51));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton confirmBtn = new JButton("Confirmar Pagamento");
        confirmBtn.setBackground(new Color(76,175,80)); confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.addActionListener(e -> {
            try {
                String method = (String) methodCombo.getSelectedItem();
                controller.efetuarPagamento(numeroMesa, method, useBonus.isSelected());
                refreshTable();
                if (parentFrame instanceof MainFrame) ((MainFrame) parentFrame).refreshSidebar();
                modal.dispose();
                String msg = "Pagamento realizado!\nMesa liberada.";
                if (cliente != null) msg += String.format("\nBônus atual: R$ %.2f", cliente.getBonus());
                JOptionPane.showMessageDialog(parentFrame, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        cp.add(btnPanel);

        modal.add(cp);
        modal.setVisible(true);
    }

    public void refresh() { refreshTable(); }
}
