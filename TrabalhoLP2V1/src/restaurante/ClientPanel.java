package restaurante;

import model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientPanel extends JPanel {
    private RestauranteController controller;
    private JFrame parentFrame;
    private JTable clientTable;
    private DefaultTableModel tableModel;

    public ClientPanel(RestauranteController controller, JFrame parentFrame) {
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

        JLabel titleLabel = new JLabel("Controle de Clientes Cadastrados");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel countLabel = new JLabel(controller.getListaClientes().size() + " clientes na base");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        countLabel.setForeground(new Color(68, 68, 68));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(245, 245, 245));
        textPanel.add(titleLabel);
        textPanel.add(countLabel);

        JButton addBtn = new JButton("+ Adicionar Novo Cliente");
        addBtn.setBackground(new Color(0, 122, 255));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.addActionListener(e -> showAddClientModal());
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"CPF", "Nome Completo", "Email", "Bônus (pts)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientTable = new JTable(tableModel);
        clientTable.setFont(new Font("Arial", Font.PLAIN, 12));
        clientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        clientTable.getTableHeader().setBackground(new Color(240, 240, 240));
        clientTable.getTableHeader().setForeground(new Color(51, 51, 51));
        clientTable.setForeground(new Color(51, 51, 51));
        clientTable.setRowHeight(25);
        refreshClientTable();

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(232, 232, 232)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshClientTable() {
        tableModel.setRowCount(0);

        for (Cliente cliente : controller.getListaClientes()) {
            String bonus = cliente.getBonus() > 0 ? 
                String.format("%.0f pts", cliente.getBonus()) : "—";
            
            tableModel.addRow(new Object[]{
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getEmail(),
                bonus
            });
        }
    }

    private void showAddClientModal() {
        JDialog modal = new JDialog((JFrame) parentFrame, "Cadastrar Novo Cliente", true);
        modal.setSize(450, 300);
        modal.setLocationRelativeTo(parentFrame);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Nome Completo:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        nameLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setForeground(new Color(51, 51, 51));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(12));

        JLabel cpfLabel = new JLabel("CPF:");
        cpfLabel.setFont(new Font("Arial", Font.BOLD, 11));
        cpfLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(cpfLabel);

        JTextField cpfField = new JTextField();
        cpfField.setForeground(new Color(51, 51, 51));
        cpfField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        cpfField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(cpfField);
        contentPanel.add(Box.createVerticalStrut(12));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 11));
        emailLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setForeground(new Color(51, 51, 51));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(emailField);

        contentPanel.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230, 230, 230));
        cancelBtn.setForeground(new Color(51, 51, 51));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton confirmBtn = new JButton("Confirmar e Cadastrar");
        confirmBtn.setBackground(new Color(0, 122, 255));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String cpf = cpfField.getText().trim();
                String email = emailField.getText().trim();

                if (name.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "Preencha todos os campos!", 
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                controller.cadastrarCliente(name, cpf, email);
                refreshClientTable();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Cliente cadastrado com sucesso!");
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
        refreshClientTable();
    }
}