package restaurante;

import model.Mesa;
import model.StatusMesa;
import model.Cliente;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardPanel extends JPanel {
    private RestauranteController controller;
    private JFrame parentFrame;
    private JPanel gridPanel;
    private Timer refreshTimer;

    public DashboardPanel(RestauranteController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        initComponents();
        startAutoRefresh();
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JLabel titleLabel = new JLabel("Status das Mesas em Tempo Real");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(titleLabel);

        int livre = controller.contarMesasLivres();
        int ocupada = controller.contarMesasOcupadas();

        JLabel statusLabel = new JLabel();
        statusLabel.setText(String.format("  🟢 %d livres     🔴 %d ocupadas", livre, ocupada));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(68, 68, 68));
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createVerticalStrut(24));

        gridPanel = new JPanel(new GridLayout(2, 5, 16, 16));
        gridPanel.setBackground(new Color(245, 245, 245));
        gridPanel.setMaximumSize(new Dimension(800, 300));

        refreshGridPanel();

        contentPanel.add(gridPanel);
        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(new Color(245, 245, 245));
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshGridPanel() {
        gridPanel.removeAll();
        for (Mesa mesa : controller.getListaMesas()) {
            JButton tableBtn = createTableButton(mesa);
            gridPanel.add(tableBtn);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JButton createTableButton(Mesa mesa) {
        JButton btn = new JButton();
        boolean isLivre = mesa.getStatus() == StatusMesa.LIVRE;
        
        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
        btn.setBackground(isLivre ? new Color(235, 247, 235) : new Color(255, 235, 230));
        btn.setBorder(BorderFactory.createLineBorder(
            isLivre ? new Color(160, 215, 160) : new Color(250, 168, 150), 2
        ));
        btn.setForeground(new Color(51, 51, 51));
        btn.setFont(new Font("JetBrains Mono", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 110));
        
        JLabel numberLabel = new JLabel(String.format("MESA %d", mesa.getNumero()));
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        numberLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 13));
        numberLabel.setForeground(new Color(51, 51, 51));
        
        JLabel statusLabelComp = new JLabel(isLivre ? "LIVRE" : "OCUPADA");
        statusLabelComp.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabelComp.setFont(new Font("Arial", Font.BOLD, 10));
        statusLabelComp.setForeground(isLivre ? new Color(76, 175, 80) : new Color(244, 67, 54));
        
        btn.add(numberLabel);
        btn.add(Box.createVerticalStrut(8));
        btn.add(statusLabelComp);
        
        if (!isLivre) {
            JLabel usageLabel = new JLabel("em uso");
            usageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            usageLabel.setFont(new Font("Arial", Font.PLAIN, 9));
            usageLabel.setForeground(new Color(244, 67, 54));
            btn.add(usageLabel);
        }

        if (isLivre) {
            btn.addActionListener(e -> showOpenTableModal(mesa.getNumero()));
        } else {
            btn.addActionListener(e -> showTableDetailsModal(mesa.getNumero()));
        }

        return btn;
    }

    private void showOpenTableModal(int numeroMesa) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Abertura de Mesa " + numeroMesa, true);
        modal.setSize(500, 380);
        modal.setLocationRelativeTo(parentFrame);
        modal.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(255, 255, 255));

        JLabel titleLabel = new JLabel(String.format("Abrir Mesa %d", numeroMesa));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        JLabel clientSelectLabel = new JLabel("Selecionar Cliente (Obrigatório):");
        clientSelectLabel.setFont(new Font("Arial", Font.BOLD, 11));
        clientSelectLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(clientSelectLabel);

        JComboBox<String> clientCombo = new JComboBox<>();
        clientCombo.addItem("— Escolha um cliente —");
        
        if (controller.getListaClientes().isEmpty()) {
            clientCombo.addItem("(nenhum cliente cadastrado)");
        } else {
            for (Cliente cliente : controller.getListaClientes()) {
                clientCombo.addItem(cliente.getNome() + " (" + cliente.getCpf() + ")");
            }
        }
        
        clientCombo.setBackground(Color.WHITE);
        clientCombo.setForeground(new Color(51, 51, 51));
        clientCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        contentPanel.add(clientCombo);
        contentPanel.add(Box.createVerticalStrut(15));

        JSeparator separator = new JSeparator();
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(15));

        JLabel newClientLabel = new JLabel("Ou Cadastrar Novo Cliente:");
        newClientLabel.setFont(new Font("Arial", Font.BOLD, 11));
        newClientLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(newClientLabel);

        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        nameLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(nameLabel);
        
        JTextField nameField = new JTextField();
        nameField.setForeground(new Color(51, 51, 51));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(8));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        emailLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(emailLabel);
        
        JTextField emailField = new JTextField();
        emailField.setForeground(new Color(51, 51, 51));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(emailField);

        contentPanel.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(new Color(255, 255, 255));
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230, 230, 230));
        cancelBtn.setForeground(new Color(51, 51, 51));
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelBtn.addActionListener(e -> modal.dispose());
        
        JButton confirmBtn = new JButton("Confirmar Abertura");
        confirmBtn.setBackground(new Color(0, 122, 255));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.addActionListener(e -> {
            try {
                int selectedIndex = clientCombo.getSelectedIndex();
                
                if (selectedIndex > 0 && controller.getListaClientes().size() > 0) {
                    Cliente cliente = controller.getListaClientes().get(selectedIndex - 1);
                    controller.abrirMesa(numeroMesa, cliente);
                    refreshGridPanel();
                    modal.dispose();
                    JOptionPane.showMessageDialog(parentFrame, 
                        String.format("Mesa %d aberta para %s!", numeroMesa, cliente.getNome()), 
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
                else if (!nameField.getText().trim().isEmpty() && !emailField.getText().trim().isEmpty()) {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String cpf = "00000000000";
                    
                    controller.cadastrarCliente(name, cpf, email);
                    Cliente novoCliente = controller.buscarClientePorNome(name);
                    controller.abrirMesa(numeroMesa, novoCliente);
                    
                    refreshGridPanel();
                    modal.dispose();
                    JOptionPane.showMessageDialog(parentFrame, 
                        String.format("Mesa %d aberta para %s (novo cliente)!", numeroMesa, name), 
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Selecione um cliente ou preencha os dados de um novo cliente!", 
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        contentPanel.add(btnPanel);

        modal.add(contentPanel);
        modal.setVisible(true);
    }

    private void showTableDetailsModal(int numeroMesa) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Detalhes da Mesa " + numeroMesa, true);
        modal.setSize(400, 200);
        modal.setLocationRelativeTo(parentFrame);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(255, 255, 255));

        model.Cliente cliente = controller.obterClienteDaMesa(numeroMesa);
        String clienteInfo = cliente != null ? cliente.getNome() : "Sem cliente";

        JLabel infoLabel = new JLabel(String.format("Mesa %d - Cliente: %s", numeroMesa, clienteInfo));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(infoLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(new Color(255, 255, 255));

        JButton viewOrderBtn = new JButton("Ver Pedido");
        viewOrderBtn.setBackground(new Color(0, 122, 255));
        viewOrderBtn.setForeground(Color.WHITE);
        viewOrderBtn.addActionListener(e -> modal.dispose());

        JButton payBtn = new JButton("Pagar e Fechar");
        payBtn.setBackground(new Color(76, 175, 80));
        payBtn.setForeground(Color.WHITE);
        payBtn.addActionListener(e -> {
            modal.dispose();
            showPaymentModal(numeroMesa);
        });

        btnPanel.add(viewOrderBtn);
        btnPanel.add(payBtn);
        contentPanel.add(btnPanel);

        modal.add(contentPanel);
        modal.setVisible(true);
    }

    private void showPaymentModal(int numeroMesa) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Pagamento - Mesa " + numeroMesa, true);
        modal.setSize(450, 450);
        modal.setLocationRelativeTo(parentFrame);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(255, 255, 255));

        double totalPedido = controller.calcularTotalPedido(numeroMesa);
        
        JLabel titleLabel = new JLabel(String.format("Pagamento - Mesa %d", numeroMesa));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        JLabel totalLabel = new JLabel(String.format("Total: R$ %.2f", totalPedido));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(totalLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        JLabel methodLabel = new JLabel("Método de Pagamento:");
        methodLabel.setFont(new Font("Arial", Font.BOLD, 11));
        methodLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(methodLabel);

        JComboBox<String> methodCombo = new JComboBox<>(
            new String[]{"Dinheiro", "Cartão Crédito", "Cartão Débito", "PIX", "Cheque"}
        );
        methodCombo.setBackground(Color.WHITE);
        methodCombo.setForeground(new Color(51, 51, 51));
        methodCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(methodCombo);
        contentPanel.add(Box.createVerticalStrut(15));

        model.Cliente cliente = controller.obterClienteDaMesa(numeroMesa);
        JCheckBox useBonus = new JCheckBox();
        
        if (cliente != null && cliente.getBonus() > 0) {
            useBonus.setText(String.format("Usar bônus (R$ %.2f disponível)", cliente.getBonus()));
            useBonus.setFont(new Font("Arial", Font.PLAIN, 11));
            useBonus.setForeground(new Color(51, 51, 51));
            useBonus.setBackground(new Color(255, 255, 255));
            contentPanel.add(useBonus);
            contentPanel.add(Box.createVerticalStrut(15));
        }

        contentPanel.add(Box.createVerticalGlue());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(new Color(255, 255, 255));
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230, 230, 230));
        cancelBtn.setForeground(new Color(51, 51, 51));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton confirmBtn = new JButton("Confirmar Pagamento");
        confirmBtn.setBackground(new Color(76, 175, 80));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.addActionListener(e -> {
            try {
                String method = (String) methodCombo.getSelectedItem();
                boolean usarBonus = useBonus.isSelected();
                
                controller.efetuarPagamento(numeroMesa, method, usarBonus);
                refreshGridPanel();
                modal.dispose();
                
                JOptionPane.showMessageDialog(parentFrame, 
                    "Pagamento realizado com sucesso!\nMesa liberada.", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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

    private void startAutoRefresh() {
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refreshGridPanel());
            }
        }, 0, 2000);
    }

    public void refresh() {
        refreshGridPanel();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }
}