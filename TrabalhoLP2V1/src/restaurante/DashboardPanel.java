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

        JLabel statusLabel = new JLabel(String.format("  🟢 %d livres     🔴 %d ocupadas", livre, ocupada));
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
        for (Mesa mesa : controller.getListaMesas())
            gridPanel.add(createTableButton(mesa));
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JButton createTableButton(Mesa mesa) {
        JButton btn = new JButton();
        boolean isLivre = mesa.getStatus() == StatusMesa.LIVRE;

        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
        btn.setBackground(isLivre ? new Color(235, 247, 235) : new Color(255, 235, 230));
        btn.setBorder(BorderFactory.createLineBorder(
            isLivre ? new Color(160, 215, 160) : new Color(250, 168, 150), 2));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 110));

        JLabel numberLabel = new JLabel(String.format("MESA %d", mesa.getNumero()));
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 13));
        numberLabel.setForeground(new Color(51, 51, 51));

        JLabel statusLabelComp = new JLabel(isLivre ? "LIVRE" : "OCUPADA");
        statusLabelComp.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabelComp.setFont(new Font("Arial", Font.BOLD, 10));
        statusLabelComp.setForeground(isLivre ? new Color(76, 175, 80) : new Color(244, 67, 54));

        btn.add(Box.createVerticalStrut(12));
        btn.add(numberLabel);
        btn.add(Box.createVerticalStrut(8));
        btn.add(statusLabelComp);

        if (!isLivre && mesa.getClienteAtual() != null) {
            JLabel clientLabel = new JLabel(truncate(mesa.getClienteAtual().getNome(), 12));
            clientLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            clientLabel.setFont(new Font("Arial", Font.PLAIN, 9));
            clientLabel.setForeground(new Color(150, 80, 40));
            btn.add(clientLabel);
        }

        if (isLivre) {
            btn.addActionListener(e -> showOpenTableModal(mesa.getNumero()));
        } else {
            btn.addActionListener(e -> showTableDetailsModal(mesa.getNumero()));
        }

        return btn;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    private void showOpenTableModal(int numeroMesa) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Abrir Mesa " + numeroMesa, true);
        modal.setSize(520, 420);
        modal.setLocationRelativeTo(parentFrame);
        modal.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Abrir Mesa " + numeroMesa);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(new Color(51, 51, 51));
        contentPanel.add(title);
        contentPanel.add(Box.createVerticalStrut(16));

        // --- Selecionar cliente existente ---
        JLabel existLabel = new JLabel("Selecionar Cliente Cadastrado:");
        existLabel.setFont(new Font("Arial", Font.BOLD, 11));
        existLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(existLabel);

        JComboBox<String> clientCombo = new JComboBox<>();
        clientCombo.addItem("— Selecione um cliente —");
        for (Cliente c : controller.getListaClientes())
            clientCombo.addItem(c.getNome() + " — CPF: " + c.getCpf());
        clientCombo.setBackground(Color.WHITE);
        clientCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        clientCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(clientCombo);
        contentPanel.add(Box.createVerticalStrut(14));

        JSeparator sep = new JSeparator();
        contentPanel.add(sep);
        contentPanel.add(Box.createVerticalStrut(14));

        // --- Cadastrar novo cliente ---
        JLabel newLabel = new JLabel("Ou Cadastrar Novo Cliente:");
        newLabel.setFont(new Font("Arial", Font.BOLD, 11));
        newLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(newLabel);

        JLabel nameL = new JLabel("Nome:"); nameL.setFont(new Font("Arial", Font.PLAIN, 10)); nameL.setForeground(new Color(51, 51, 51));
        contentPanel.add(nameL);
        JTextField nameField = new JTextField();
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(6));

        JLabel cpfL = new JLabel("CPF (somente números):"); cpfL.setFont(new Font("Arial", Font.PLAIN, 10)); cpfL.setForeground(new Color(51, 51, 51));
        contentPanel.add(cpfL);
        JTextField cpfField = new JTextField();
        cpfField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        cpfField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(cpfField);
        contentPanel.add(Box.createVerticalStrut(6));

        JLabel emailL = new JLabel("Email:"); emailL.setFont(new Font("Arial", Font.PLAIN, 10)); emailL.setForeground(new Color(51, 51, 51));
        contentPanel.add(emailL);
        JTextField emailField = new JTextField();
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(emailField);
        contentPanel.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setBackground(new Color(230, 230, 230));
        cancelBtn.setForeground(new Color(51, 51, 51));
        cancelBtn.addActionListener(e -> modal.dispose());

        JButton confirmBtn = new JButton("Confirmar Abertura");
        confirmBtn.setBackground(new Color(0, 122, 255));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.addActionListener(e -> {
            try {
                int idx = clientCombo.getSelectedIndex();
                if (idx > 0) {
                    Cliente cliente = controller.getListaClientes().get(idx - 1);
                    controller.abrirMesa(numeroMesa, cliente);
                    refreshGridPanel();
                    if (parentFrame instanceof MainFrame) ((MainFrame) parentFrame).refreshSidebar();
                    modal.dispose();
                    JOptionPane.showMessageDialog(parentFrame,
                        "Mesa " + numeroMesa + " aberta para " + cliente.getNome() + "!\nBônus disponível: R$ " + String.format("%.2f", cliente.getBonus()),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String nome = nameField.getText().trim();
                    String cpf = cpfField.getText().replaceAll("[^0-9]", "");
                    String email = emailField.getText().trim();
                    if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, "Selecione um cliente ou preencha todos os campos do novo cliente!", "Atenção", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    controller.cadastrarCliente(nome, cpf, email);
                    Cliente novo = controller.buscarClientePorNome(nome);
                    controller.abrirMesa(numeroMesa, novo);
                    refreshGridPanel();
                    if (parentFrame instanceof MainFrame) ((MainFrame) parentFrame).refreshSidebar();
                    modal.dispose();
                    JOptionPane.showMessageDialog(parentFrame, "Mesa " + numeroMesa + " aberta para " + nome + " (novo cliente)!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IllegalStateException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        contentPanel.add(btnPanel);

        modal.add(contentPanel);
        modal.setVisible(true);
    }

    private void showTableDetailsModal(int numeroMesa) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Mesa " + numeroMesa, true);
        modal.setSize(480, 380);
        modal.setLocationRelativeTo(parentFrame);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        Cliente cliente = controller.obterClienteDaMesa(numeroMesa);
        model.Pedido pedido = controller.buscarPedidoDaMesa(numeroMesa);

        JLabel titleLbl = new JLabel("Mesa " + numeroMesa + " — " + (cliente != null ? cliente.getNome() : "Sem cliente"));
        titleLbl.setFont(new Font("Arial", Font.BOLD, 14));
        titleLbl.setForeground(new Color(51, 51, 51));
        contentPanel.add(titleLbl);

        if (cliente != null) {
            JLabel bonusLbl = new JLabel(String.format("Bônus disponível: R$ %.2f", cliente.getBonus()));
            bonusLbl.setFont(new Font("Arial", Font.PLAIN, 11));
            bonusLbl.setForeground(new Color(76, 175, 80));
            contentPanel.add(bonusLbl);
        }
        contentPanel.add(Box.createVerticalStrut(10));

        if (pedido != null && !pedido.getItens().isEmpty()) {
            JLabel itensTitle = new JLabel("Itens do Pedido:");
            itensTitle.setFont(new Font("Arial", Font.BOLD, 11));
            itensTitle.setForeground(new Color(51, 51, 51));
            contentPanel.add(itensTitle);

            for (model.ItemPedido ip : pedido.getItens()) {
                JLabel itemLbl = new JLabel("  • " + ip.toString());
                itemLbl.setFont(new Font("Arial", Font.PLAIN, 11));
                itemLbl.setForeground(new Color(60, 60, 60));
                contentPanel.add(itemLbl);
            }
            contentPanel.add(Box.createVerticalStrut(8));

            JLabel totalLbl = new JLabel(String.format("Total: R$ %.2f", pedido.calcularTotal()));
            totalLbl.setFont(new Font("Arial", Font.BOLD, 12));
            totalLbl.setForeground(new Color(51, 51, 51));
            contentPanel.add(totalLbl);

            JLabel statusLbl = new JLabel("Status: " + pedido.getStatus());
            statusLbl.setFont(new Font("Arial", Font.PLAIN, 11));
            statusLbl.setForeground(new Color(100, 100, 200));
            contentPanel.add(statusLbl);

            JLabel tempoLbl = new JLabel("Tempo na mesa: " + pedido.obterTempoFormatado());
            tempoLbl.setFont(new Font("Arial", Font.PLAIN, 11));
            tempoLbl.setForeground(new Color(120, 120, 120));
            contentPanel.add(tempoLbl);
        } else {
            JLabel emptyLbl = new JLabel("Nenhum item no pedido ainda.");
            emptyLbl.setFont(new Font("Arial", Font.ITALIC, 11));
            emptyLbl.setForeground(new Color(150, 150, 150));
            contentPanel.add(emptyLbl);
        }

        contentPanel.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton closeBtn = new JButton("Fechar");
        closeBtn.setBackground(new Color(230, 230, 230));
        closeBtn.setForeground(new Color(51, 51, 51));
        closeBtn.addActionListener(e -> modal.dispose());

        JButton payBtn = new JButton("Pagar e Fechar Mesa");
        payBtn.setBackground(new Color(76, 175, 80));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Arial", Font.BOLD, 12));
        payBtn.addActionListener(e -> {
            modal.dispose();
            showPaymentModal(numeroMesa);
        });

        btnPanel.add(closeBtn);
        btnPanel.add(payBtn);
        contentPanel.add(btnPanel);

        modal.add(contentPanel);
        modal.setVisible(true);
    }

    private void showPaymentModal(int numeroMesa) {
        JDialog modal = new JDialog((JFrame) parentFrame, "Pagamento — Mesa " + numeroMesa, true);
        modal.setSize(450, 380);
        modal.setLocationRelativeTo(parentFrame);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        double totalPedido = controller.calcularTotalPedido(numeroMesa);
        Cliente cliente = controller.obterClienteDaMesa(numeroMesa);

        JLabel titleLabel = new JLabel("Pagamento — Mesa " + numeroMesa);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(12));

        if (cliente != null) {
            JLabel clienteLbl = new JLabel("Cliente: " + cliente.getNome());
            clienteLbl.setFont(new Font("Arial", Font.PLAIN, 11));
            clienteLbl.setForeground(new Color(80, 80, 80));
            contentPanel.add(clienteLbl);
        }

        JLabel totalLabel = new JLabel(String.format("Total do Pedido: R$ %.2f", totalPedido));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 13));
        totalLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(totalLabel);
        contentPanel.add(Box.createVerticalStrut(14));

        JLabel methodLabel = new JLabel("Forma de Pagamento:");
        methodLabel.setFont(new Font("Arial", Font.BOLD, 11));
        methodLabel.setForeground(new Color(51, 51, 51));
        contentPanel.add(methodLabel);

        JComboBox<String> methodCombo = new JComboBox<>(
            new String[]{"Cartão de Crédito", "Cartão de Débito", "Dinheiro", "PIX"});
        methodCombo.setBackground(Color.WHITE);
        methodCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        contentPanel.add(methodCombo);
        contentPanel.add(Box.createVerticalStrut(14));

        JCheckBox useBonus = new JCheckBox();
        if (cliente != null && cliente.getBonus() > 0) {
            useBonus.setText(String.format("Usar bônus acumulado (R$ %.2f disponível)", cliente.getBonus()));
            useBonus.setFont(new Font("Arial", Font.PLAIN, 11));
            useBonus.setBackground(Color.WHITE);
            useBonus.setForeground(new Color(51, 51, 51));
            contentPanel.add(useBonus);

            // Preview do desconto
            JLabel previewLbl = new JLabel();
            previewLbl.setFont(new Font("Arial", Font.ITALIC, 10));
            previewLbl.setForeground(new Color(76, 175, 80));
            double desc = Math.min(cliente.getBonus(), totalPedido);
            previewLbl.setText(String.format("  → Desconto de R$ %.2f | Total final: R$ %.2f", desc, totalPedido - desc));
            useBonus.addActionListener(e -> {
                if (useBonus.isSelected()) previewLbl.setText(String.format("  → Desconto de R$ %.2f | Total final: R$ %.2f", desc, totalPedido - desc));
                else previewLbl.setText("");
            });
            contentPanel.add(previewLbl);
            contentPanel.add(Box.createVerticalStrut(8));
        }

        if (cliente != null) {
            JLabel bonusInfo = new JLabel("Você acumulará 10% do valor pago em bônus!");
            bonusInfo.setFont(new Font("Arial", Font.ITALIC, 10));
            bonusInfo.setForeground(new Color(100, 100, 200));
            contentPanel.add(bonusInfo);
        }

        contentPanel.add(Box.createVerticalGlue());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
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
                boolean usarBonusVal = useBonus.isSelected();
                double bonusAntesCliente = cliente != null ? cliente.getBonus() : 0;

                controller.efetuarPagamento(numeroMesa, method, usarBonusVal);
                refreshGridPanel();
                if (parentFrame instanceof MainFrame) ((MainFrame) parentFrame).refreshSidebar();
                modal.dispose();

                String msg = "Pagamento realizado com sucesso!\nMesa liberada.";
                if (cliente != null) {
                    double novoBonus = cliente.getBonus();
                    msg += String.format("\n\nBônus acumulado: R$ %.2f", novoBonus);
                }
                JOptionPane.showMessageDialog(parentFrame, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

    public void refresh() { refreshGridPanel(); }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null) refreshTimer.cancel();
    }
}
