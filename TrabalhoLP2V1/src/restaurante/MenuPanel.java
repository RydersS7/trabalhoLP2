package restaurante;

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

        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"Item", "Categoria", "Preço", "Descrição / Especificação"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        menuTable = new JTable(tableModel);
        menuTable.setFont(new Font("Arial", Font.PLAIN, 12));
        menuTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        menuTable.getTableHeader().setBackground(new Color(240, 240, 240));
        menuTable.getTableHeader().setForeground(new Color(51, 51, 51));
        menuTable.setForeground(new Color(51, 51, 51));
        menuTable.setRowHeight(28);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        refreshMenuTable();

        menuTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = menuTable.getSelectedRow();
                    if (row >= 0 && row < controller.getListaCardapio().size())
                        showItemOptionsModal(controller.getListaCardapio().get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(232, 232, 232)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        bottomPanel.setBackground(new Color(245, 245, 245));
        JLabel hint = new JLabel("💡 Duplo clique em um item para editar ou excluir");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(120, 120, 120));
        bottomPanel.add(hint);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshMenuTable() {
        tableModel.setRowCount(0);
        for (ItemCardapio item : controller.getListaCardapio()) {
            // Polimorfismo: getDescricaoCompleta() é chamado via referência abstrata
            tableModel.addRow(new Object[]{
                item.getNome(),
                item.getCategoria(),
                String.format("R$ %.2f", item.getPreco()),
                item.getDescricaoCompleta()
            });
        }
    }

    private void showAddMenuItemModal() {
        JDialog modal = new JDialog((JFrame) parentFrame, "Adicionar Item ao Cardápio", true);
        modal.setSize(480, 420);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cp.setBackground(Color.WHITE);

        JLabel catLbl = new JLabel("Categoria:");
        catLbl.setFont(new Font("Arial", Font.BOLD, 11)); catLbl.setForeground(new Color(51,51,51));
        cp.add(catLbl);

        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Comida", "Bebida"});
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cp.add(categoryCombo);
        cp.add(Box.createVerticalStrut(10));

        JLabel nameLbl = new JLabel("Nome:");
        nameLbl.setFont(new Font("Arial", Font.BOLD, 11)); nameLbl.setForeground(new Color(51,51,51));
        cp.add(nameLbl);
        JTextField nameField = new JTextField();
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cp.add(nameField);
        cp.add(Box.createVerticalStrut(10));

        JLabel priceLbl = new JLabel("Preço (R$):");
        priceLbl.setFont(new Font("Arial", Font.BOLD, 11)); priceLbl.setForeground(new Color(51,51,51));
        cp.add(priceLbl);
        JTextField priceField = new JTextField();
        priceField.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        priceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cp.add(priceField);
        cp.add(Box.createVerticalStrut(10));

        // Dynamic label based on category
        JLabel specLbl = new JLabel("Descrição do prato:");
        specLbl.setFont(new Font("Arial", Font.BOLD, 11)); specLbl.setForeground(new Color(51,51,51));
        cp.add(specLbl);
        JTextField specField = new JTextField();
        specField.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        specField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cp.add(specField);
        cp.add(Box.createVerticalStrut(6));

        JLabel spec2Lbl = new JLabel("Ingredientes (separados por vírgula):");
        spec2Lbl.setFont(new Font("Arial", Font.BOLD, 11)); spec2Lbl.setForeground(new Color(51,51,51));
        cp.add(spec2Lbl);
        JTextField spec2Field = new JTextField();
        spec2Field.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        spec2Field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cp.add(spec2Field);

        // Toggle fields based on category
        categoryCombo.addActionListener(e -> {
            boolean isComida = "Comida".equals(categoryCombo.getSelectedItem());
            specLbl.setText(isComida ? "Descrição do prato:" : "Fornecedor:");
            spec2Lbl.setText(isComida ? "Ingredientes (separados por vírgula):" : "Volume (ex: 350ml):");
        });

        cp.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230,230,230)); cancelBtn.setForeground(new Color(51,51,51));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton confirmBtn = new JButton("Adicionar");
        confirmBtn.setBackground(new Color(0,122,255)); confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.addActionListener(e -> {
            try {
                String nome = nameField.getText().trim();
                double preco = Double.parseDouble(priceField.getText().trim().replace(",", "."));
                String category = (String) categoryCombo.getSelectedItem();
                String spec1 = specField.getText().trim();
                String spec2 = spec2Field.getText().trim();

                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "Preencha o nome do item!", "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if ("Comida".equals(category)) {
                    String[] ings = spec2.isEmpty() ? new String[0] : spec2.split(",");
                    controller.adicionarComida(nome, preco, spec1, ings);
                } else {
                    controller.adicionarBebida(nome, preco, spec1, spec2);
                }

                refreshMenuTable();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Item adicionado com sucesso!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Preço inválido! Use formato: 10.50", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        cp.add(btnPanel);

        modal.add(cp);
        modal.setVisible(true);
    }

    private void showItemOptionsModal(ItemCardapio item) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Item: " + item.getNome(), true);
        modal.setSize(440, 260);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cp.setBackground(Color.WHITE);

        JLabel nameLbl = new JLabel(item.getNome());
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14)); nameLbl.setForeground(new Color(51,51,51));
        cp.add(nameLbl);
        JLabel catLbl = new JLabel(item.toString());
        catLbl.setFont(new Font("Arial", Font.PLAIN, 11)); catLbl.setForeground(new Color(100,100,100));
        cp.add(catLbl);
        JLabel descLbl = new JLabel(item.getDescricaoCompleta());
        descLbl.setFont(new Font("Arial", Font.PLAIN, 11)); descLbl.setForeground(new Color(80,80,80));
        cp.add(descLbl);
        cp.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton editBtn = new JButton("✏ Editar");
        editBtn.setBackground(new Color(0,122,255)); editBtn.setForeground(Color.WHITE);
        editBtn.addActionListener(e -> {
            modal.dispose();
            showEditItemModal(item);
        });

        JButton deleteBtn = new JButton("🗑 Excluir");
        deleteBtn.setBackground(new Color(220,53,69)); deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Excluir \"" + item.getNome() + "\" do cardápio?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.removerItemCardapio(item);
                refreshMenuTable();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Item removido com sucesso!");
            }
        });

        JButton closeBtn = new JButton("Fechar");
        closeBtn.setBackground(new Color(200,200,200)); closeBtn.setForeground(new Color(51,51,51));
        closeBtn.addActionListener(e -> modal.dispose());

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(closeBtn);
        cp.add(btnPanel);

        modal.add(cp);
        modal.setVisible(true);
    }

    private void showEditItemModal(ItemCardapio item) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Editar Item", true);
        modal.setSize(420, 280);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cp.setBackground(Color.WHITE);

        JLabel nameLbl = new JLabel("Nome:");
        nameLbl.setFont(new Font("Arial", Font.BOLD, 11)); nameLbl.setForeground(new Color(51,51,51));
        cp.add(nameLbl);
        JTextField nameField = new JTextField(item.getNome());
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cp.add(nameField);
        cp.add(Box.createVerticalStrut(10));

        JLabel priceLbl = new JLabel("Preço (R$):");
        priceLbl.setFont(new Font("Arial", Font.BOLD, 11)); priceLbl.setForeground(new Color(51,51,51));
        cp.add(priceLbl);
        JTextField priceField = new JTextField(String.format("%.2f", item.getPreco()));
        priceField.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        priceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cp.add(priceField);
        cp.add(Box.createVerticalStrut(10));

        String specLabel = item instanceof Comida ? "Descrição:" : "Volume:";
        String specValue = item instanceof Comida ? ((Comida) item).getDescricao() : ((Bebida) item).getVolume();
        JLabel specLbl = new JLabel(specLabel);
        specLbl.setFont(new Font("Arial", Font.BOLD, 11)); specLbl.setForeground(new Color(51,51,51));
        cp.add(specLbl);
        JTextField specField = new JTextField(specValue != null ? specValue : "");
        specField.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        specField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cp.add(specField);
        cp.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230,230,230)); cancelBtn.setForeground(new Color(51,51,51));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton saveBtn = new JButton("Salvar");
        saveBtn.setBackground(new Color(0,122,255)); saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 12));
        saveBtn.addActionListener(e -> {
            try {
                String novoNome = nameField.getText().trim();
                double novoPreco = Double.parseDouble(priceField.getText().trim().replace(",", "."));
                String novaSpec = specField.getText().trim();
                controller.alterarItemCardapio(item, novoNome, novoPreco, novaSpec);
                refreshMenuTable();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Item atualizado com sucesso!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Preço inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        cp.add(btnPanel);

        modal.add(cp);
        modal.setVisible(true);
    }

    public void refresh() { refreshMenuTable(); }
}
