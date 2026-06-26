package view.panels;

import controller.RestauranteController;
import model.ItemCardapio;
import model.Comida;
import model.Bebida;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MenuPanel extends JPanel {
    private RestauranteController controller;
    private JFrame parentFrame;
    private JTable menuTable;
    private DefaultTableModel tableModel;

    public MenuPanel(RestauranteController controller, JFrame parentFrame) {
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

        JLabel titleLabel = new JLabel("Cardápio de Alimentos e Bebidas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel countLabel = new JLabel(controller.getListaCardapio().size() + " itens cadastrados");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        countLabel.setForeground(new Color(68, 68, 68));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(245, 245, 245));
        textPanel.add(titleLabel);
        textPanel.add(countLabel);

        JButton addBtn = new JButton("+ Adicionar ao Cardápio");
        addBtn.setBackground(new Color(0, 122, 255));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.addActionListener(e -> showAddMenuItemModal());
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"Item", "Categoria", "Preço Base", "Especificação"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        menuTable = new JTable(tableModel);
        menuTable.setFont(new Font("Arial", Font.PLAIN, 12));
        menuTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        menuTable.getTableHeader().setBackground(new Color(240, 240, 240));
        menuTable.getTableHeader().setForeground(new Color(51, 51, 51));
        menuTable.setForeground(new Color(51, 51, 51));
        menuTable.setRowHeight(25);
        refreshMenuTable();

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(232, 232, 232)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshMenuTable() {
        tableModel.setRowCount(0);

        for (ItemCardapio item : controller.getListaCardapio()) {
            String categoria = item instanceof Comida ? "Comida" : "Bebida";
            tableModel.addRow(new Object[]{
                item.getNome(),
                categoria,
                "R$ " + String.format("%.2f", item.getPreco()),
                ""
            });
        }
    }

    private void showAddMenuItemModal() {
        JDialog modal = new JDialog((JFrame) parentFrame, "Adicionar Item ao Cardápio", true);
        modal.setSize(450, 360);
        modal.setLocationRelativeTo(parentFrame);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        JLabel categoryLabel = new JLabel("Categoria:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 11));
        categoryLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(categoryLabel);

        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Comida", "Bebida"});
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setForeground(new Color(51, 51, 51));
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(categoryCombo);
        contentPanel.add(Box.createVerticalStrut(12));

        JLabel nameLabel = new JLabel("Nome do Item:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        nameLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setForeground(new Color(51, 51, 51));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(12));

        JLabel priceLabel = new JLabel("Preço Base (R$):");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 11));
        priceLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(priceLabel);

        JTextField priceField = new JTextField();
        priceField.setForeground(new Color(51, 51, 51));
        priceField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        priceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(priceField);
        contentPanel.add(Box.createVerticalStrut(12));

        JLabel specLabel = new JLabel("Especificação:");
        specLabel.setFont(new Font("Arial", Font.BOLD, 11));
        specLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(specLabel);

        JTextField specField = new JTextField();
        specField.setForeground(new Color(51, 51, 51));
        specField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        specField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(specField);

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
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String category = (String) categoryCombo.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "Preenchao nome do item!", 
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (category.equals("Comida")) {
                    controller.adicionarComida(name, price, specField.getText());
                } else {
                    controller.adicionarBebida(name, price, "", specField.getText());
                }

                refreshMenuTable();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Item adicionado com sucesso!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Preço inválido! Use formato: 10.50", 
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
        refreshMenuTable();
    }
}