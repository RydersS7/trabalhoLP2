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
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 16, 32));

        JLabel titleLabel = new JLabel("Monitor de Contas e Pedidos Ativos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel countLabel = new JLabel(
            String.format("%d mesa%s com conta aberta", 
                controller.getListaPedidosAtivos().size(),
                controller.getListaPedidosAtivos().size() != 1 ? "s" : "")
        );
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        countLabel.setForeground(new Color(68, 68, 68));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(245, 245, 245));
        textPanel.add(titleLabel);
        textPanel.add(countLabel);

        JButton addBtn = new JButton("+ Adicionar Item a uma Mesa");
        addBtn.setBackground(new Color(0, 122, 255));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.addActionListener(e -> showAddItemModal());
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mesa", "Cliente", "Itens Solicitados", "Valor Total", "Ações"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ordersTable.getTableHeader().setBackground(new Color(240, 240, 240));
        ordersTable.getTableHeader().setForeground(new Color(51, 51, 51));
        ordersTable.setForeground(new Color(51, 51, 51));
        ordersTable.setRowHeight(40);
        
        ordersTable.getColumnModel().getColumn(4).setMaxWidth(100);
        
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(232, 232, 232)));
        add(scrollPane, BorderLayout.CENTER);
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
            
            tableModel.addRow(new Object[]{mesa, cliente, itens.toString(), total, "Pagar"});
        }
    }

    private void showAddItemModal() {
        if (controller.getListaPedidosAtivos().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Nenhuma mesa com conta aberta!", 
                "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog modal = new JDialog((JFrame) parentFrame, "Adicionar Item a uma Mesa", true);
        modal.setSize(450, 340);
        modal.setLocationRelativeTo(parentFrame);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        JLabel mesaLabel = new JLabel("Selecionar Mesa:");
        mesaLabel.setFont(new Font("Arial", Font.BOLD, 11));
        mesaLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(mesaLabel);

        JComboBox<String> mesaCombo = new JComboBox<>();
        for (Pedido pedido : controller.getListaPedidosAtivos()) {
            mesaCombo.addItem("Mesa " + pedido.getMesa().getNumero());
        }
        mesaCombo.setBackground(Color.WHITE);
        mesaCombo.setForeground(new Color(51, 51, 51));
        mesaCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        mesaCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(mesaCombo);
        contentPanel.add(Box.createVerticalStrut(12));

        JLabel itemLabel = new JLabel("Selecionar Item:");
        itemLabel.setFont(new Font("Arial", Font.BOLD, 11));
        itemLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(itemLabel);

        JComboBox<String> itemCombo = new JComboBox<>();
        for (ItemCardapio item : controller.getListaCardapio()) {
            itemCombo.addItem(item.getNome() + " — R$ " + String.format("%.2f", item.getPreco()));
        }
        itemCombo.setBackground(Color.WHITE);
        itemCombo.setForeground(new Color(51, 51, 51));
        itemCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        itemCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(itemCombo);
        contentPanel.add(Box.createVerticalStrut(12));

        JLabel qtyLabel = new JLabel("Quantidade:");
        qtyLabel.setFont(new Font("Arial", Font.BOLD, 11));
        qtyLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(qtyLabel);

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        qtySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(qtySpinner);

        contentPanel.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230, 230, 230));
        cancelBtn.setForeground(new Color(51, 51, 51));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton confirmBtn = new JButton("Confirmar e Adicionar");
        confirmBtn.setBackground(new Color(0, 122, 255));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.addActionListener(e -> {
            try {
                int mesaIndex = mesaCombo.getSelectedIndex();
                int itemIndex = itemCombo.getSelectedIndex();
                int qtd = (Integer) qtySpinner.getValue();

                if (mesaIndex >= 0 && itemIndex >= 0) {
                    Pedido pedido = controller.getListaPedidosAtivos().get(mesaIndex);
                    ItemCardapio item = controller.getListaCardapio().get(itemIndex);
                    
                    controller.adicionarItemAoPedido(pedido.getMesa().getNumero(), item, qtd);
                    refreshTable();
                    modal.dispose();
                    JOptionPane.showMessageDialog(parentFrame, "Item adicionado com sucesso!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        contentPanel.add(btnPanel);

        modal.add(contentPanel);
        modal.setVisible(true);
    }

    public void refresh() {
        refreshTable();
    }
}