package restaurante;

import model.Cliente;
import model.Pedido;
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

        JPanel btnBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBarPanel.setBackground(new Color(245, 245, 245));

        JButton searchBtn = styledBtn("🔍 Consultar", new Color(100, 100, 180));
        searchBtn.addActionListener(e -> showSearchClientModal());

        JButton addBtn = styledBtn("+ Adicionar Cliente", new Color(0, 122, 255));
        addBtn.addActionListener(e -> showAddClientModal());

        btnBarPanel.add(searchBtn);
        btnBarPanel.add(addBtn);

        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(btnBarPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"CPF", "Nome Completo", "Email", "Bônus (R$)", "Total Gasto (R$)", "Pedidos"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        clientTable = new JTable(tableModel);
        clientTable.setFont(new Font("Arial", Font.PLAIN, 12));
        clientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        clientTable.getTableHeader().setBackground(new Color(240, 240, 240));
        clientTable.getTableHeader().setForeground(new Color(51, 51, 51));
        clientTable.setForeground(new Color(51, 51, 51));
        clientTable.setRowHeight(28);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshClientTable();

        // Duplo clique para editar
        clientTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = clientTable.getSelectedRow();
                    if (row >= 0 && row < controller.getListaClientes().size())
                        showClientOptionsModal(controller.getListaClientes().get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(232, 232, 232)));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        bottomPanel.setBackground(new Color(245, 245, 245));
        JLabel hint = new JLabel("💡 Duplo clique em um cliente para editar, excluir ou ver histórico");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(120, 120, 120));
        bottomPanel.add(hint);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshClientTable() {
        tableModel.setRowCount(0);
        for (Cliente c : controller.getListaClientes()) {
            int numPedidos = c.getHistoricoPedidos().size();
            tableModel.addRow(new Object[]{
                c.getCpf(),
                c.getNome(),
                c.getEmail(),
                String.format("R$ %.2f", c.getBonus()),
                String.format("R$ %.2f", c.getTotalGasto()),
                numPedidos + " pedido(s)"
            });
        }
    }

    private void showAddClientModal() {
        JDialog modal = new JDialog((JFrame) parentFrame, "Cadastrar Novo Cliente", true);
        modal.setSize(450, 310);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cp.setBackground(Color.WHITE);

        JTextField nameField = addField(cp, "Nome Completo:");
        JTextField cpfField = addField(cp, "CPF (somente números):");
        JTextField emailField = addField(cp, "Email:");

        cp.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        JButton cancelBtn = styledBtn("Cancelar", new Color(140, 145, 165));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton confirmBtn = styledBtn("Cadastrar", new Color(0, 122, 255));
        confirmBtn.addActionListener(e -> {
            try {
                String nome = nameField.getText().trim();
                String cpf = cpfField.getText().replaceAll("[^0-9]", "");
                String email = emailField.getText().trim();
                if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "Preencha todos os campos!", "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                controller.cadastrarCliente(nome, cpf, email);
                refreshClientTable();
                if (parentFrame instanceof MainFrame) ((MainFrame) parentFrame).refreshSidebar();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Cliente cadastrado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        cp.add(btnPanel);

        modal.add(cp);
        ThemeManager.apply(modal);
        modal.setVisible(true);
    }

    private void showSearchClientModal() {
        String query = JOptionPane.showInputDialog(parentFrame, "Pesquisar por nome ou CPF:", "Consultar Cliente", JOptionPane.QUESTION_MESSAGE);
        if (query == null || query.trim().isEmpty()) return;
        String q = query.trim();

        Cliente found = controller.buscarClientePorNome(q);
        if (found == null) found = controller.buscarClientePorCpf(q);
        if (found == null) {
            JOptionPane.showMessageDialog(parentFrame, "Nenhum cliente encontrado para: " + q, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showClientOptionsModal(found);
        }
    }

    private void showClientOptionsModal(Cliente cliente) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Cliente: " + cliente.getNome(), true);
        modal.setSize(500, 460);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cp.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel(cliente.getNome());
        titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
        titleLbl.setForeground(new Color(51, 51, 51));
        cp.add(titleLbl);

        JLabel cpfLbl = new JLabel("CPF: " + cliente.getCpf());
        cpfLbl.setFont(new Font("Arial", Font.PLAIN, 11)); cpfLbl.setForeground(new Color(100,100,100));
        cp.add(cpfLbl);

        JLabel emailLbl = new JLabel("Email: " + cliente.getEmail());
        emailLbl.setFont(new Font("Arial", Font.PLAIN, 11)); emailLbl.setForeground(new Color(100,100,100));
        cp.add(emailLbl);

        cp.add(Box.createVerticalStrut(10));

        JLabel bonusLbl = new JLabel(String.format("💰 Bônus Acumulado: R$ %.2f", cliente.getBonus()));
        bonusLbl.setFont(new Font("Arial", Font.BOLD, 13));
        bonusLbl.setForeground(new Color(76, 175, 80));
        cp.add(bonusLbl);

        JLabel gastoLbl = new JLabel(String.format("📊 Total Gasto: R$ %.2f em %d pedido(s)", cliente.getTotalGasto(), cliente.getHistoricoPedidos().size()));
        gastoLbl.setFont(new Font("Arial", Font.PLAIN, 11)); gastoLbl.setForeground(new Color(80,80,80));
        cp.add(gastoLbl);

        cp.add(Box.createVerticalStrut(12));

        // Histórico de pedidos
        if (!cliente.getHistoricoPedidos().isEmpty()) {
            JLabel histTitle = new JLabel("Histórico de Pedidos:");
            histTitle.setFont(new Font("Arial", Font.BOLD, 11));
            histTitle.setForeground(new Color(51, 51, 51));
            cp.add(histTitle);

            JTextArea histArea = new JTextArea();
            histArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
            histArea.setEditable(false);
            histArea.setBackground(new Color(248, 248, 248));
            histArea.setForeground(new Color(51, 51, 51));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cliente.getHistoricoPedidos().size(); i++) {
                Pedido p = cliente.getHistoricoPedidos().get(i);
                sb.append(String.format("Pedido #%d — Mesa %d — %s — R$ %.2f\n",
                    i + 1,
                    p.getMesa() != null ? p.getMesa().getNumero() : 0,
                    p.getStatus(),
                    p.calcularTotal()));
                for (model.ItemPedido ip : p.getItens())
                    sb.append(String.format("   %dx %s\n", ip.getQuantidade(), ip.getItem().getNome()));
            }
            histArea.setText(sb.toString());
            JScrollPane histScroll = new JScrollPane(histArea);
            histScroll.setPreferredSize(new Dimension(0, 120));
            histScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            cp.add(histScroll);
        }

        cp.add(Box.createVerticalStrut(14));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton editBtn = styledBtn("✏ Editar", new Color(0, 122, 255));
        editBtn.addActionListener(e -> {
            modal.dispose();
            showEditClientModal(cliente);
        });

        JButton deleteBtn = styledBtn("🗑 Excluir", new Color(220, 53, 69));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Tem certeza que deseja excluir o cliente " + cliente.getNome() + "?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.removerCliente(cliente);
                refreshClientTable();
                if (parentFrame instanceof MainFrame) ((MainFrame) parentFrame).refreshSidebar();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Cliente excluído com sucesso!");
            }
        });

        JButton closeBtn = styledBtn("Fechar", new Color(140, 145, 165));
        closeBtn.addActionListener(e -> modal.dispose());

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(closeBtn);
        cp.add(btnPanel);

        modal.add(cp);
        ThemeManager.apply(modal);
        modal.setVisible(true);
    }

    private void showEditClientModal(Cliente cliente) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Editar Cliente", true);
        modal.setSize(420, 250);
        modal.setLocationRelativeTo(parentFrame);

        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        cp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cp.setBackground(Color.WHITE);

        JTextField nameField = addField(cp, "Nome Completo:");
        nameField.setText(cliente.getNome());
        JTextField emailField = addField(cp, "Email:");
        emailField.setText(cliente.getEmail());

        cp.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton cancelBtn = styledBtn("Cancelar", new Color(140, 145, 165));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton saveBtn = styledBtn("Salvar Alterações", new Color(0, 122, 255));
        saveBtn.addActionListener(e -> {
            try {
                String novoNome = nameField.getText().trim();
                String novoEmail = emailField.getText().trim();
                if (novoNome.isEmpty() || novoEmail.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "Preencha todos os campos!", "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                controller.alterarCliente(cliente, novoNome, novoEmail);
                refreshClientTable();
                modal.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Cliente atualizado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        cp.add(btnPanel);

        modal.add(cp);
        ThemeManager.apply(modal);
        modal.setVisible(true);
    }

    private JTextField addField(JPanel panel, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        lbl.setForeground(new Color(51, 51, 51));
        panel.add(lbl);
        JTextField field = new JTextField();
        field.setForeground(new Color(51, 51, 51));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
        return field;
    }

    public void refresh() { refreshClientTable(); }

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

}