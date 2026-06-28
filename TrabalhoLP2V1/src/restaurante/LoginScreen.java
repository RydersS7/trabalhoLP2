package restaurante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginScreen extends JFrame {

    private RestauranteController controller;

    // Card panel references for switching login / register
    private JPanel cardContainer;
    private CardLayout cardLayout;

    public LoginScreen(RestauranteController controller) {
        this.controller = controller;
        setTitle("MEZA — Bem-vindo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 246, 250));
        setContentPane(root);

        root.add(buildLeftPanel(), BorderLayout.WEST);

        // Right side uses CardLayout to flip between Login and Register
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(new Color(245, 246, 250));
        cardContainer.add(buildLoginCard(), "login");
        cardContainer.add(buildRegisterCard(), "register");
        cardLayout.show(cardContainer, "login");

        root.add(cardContainer, BorderLayout.CENTER);
    }

    // ── LEFT BRANDING PANEL ──────────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(18, 18, 24),
                        getWidth(), getHeight(), new Color(28, 30, 46));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setColor(new Color(0, 122, 255));
                g2.fillOval(-60, -60, 260, 260);
                g2.fillOval(40, getHeight() - 190, 230, 230);
                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(56, 38, 56, 38));

        // Logo
        JLabel logoIcon = new JLabel("⊞") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 122, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setFont(new Font("Arial", Font.BOLD, 20));
        logoIcon.setHorizontalAlignment(SwingConstants.CENTER);
        logoIcon.setVerticalAlignment(SwingConstants.CENTER);
        logoIcon.setOpaque(false);
        logoIcon.setPreferredSize(new Dimension(48, 48));
        logoIcon.setMaximumSize(new Dimension(48, 48));
        logoIcon.setMinimumSize(new Dimension(48, 48));
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandName = new JLabel("MEZA");
        brandName.setForeground(Color.WHITE);
        brandName.setFont(new Font("Arial", Font.BOLD, 32));
        brandName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("Gestão de Restaurante");
        brandSub.setForeground(new Color(140, 145, 170));
        brandSub.setFont(new Font("Arial", Font.PLAIN, 12));
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(logoIcon);
        panel.add(Box.createVerticalStrut(16));
        panel.add(brandName);
        panel.add(Box.createVerticalStrut(3));
        panel.add(brandSub);
        panel.add(Box.createVerticalGlue());

        String[] feats = {"Mesas em tempo real", "Cardápio digital", "Programa de fidelidade", "Visão da cozinha"};
        for (String f : feats) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(240, 26));
            JLabel dot = new JLabel("• ");
            dot.setForeground(new Color(0, 122, 255));
            dot.setFont(new Font("Arial", Font.BOLD, 13));
            JLabel txt = new JLabel(f);
            txt.setForeground(new Color(175, 180, 200));
            txt.setFont(new Font("Arial", Font.PLAIN, 12));
            row.add(dot); row.add(txt);
            panel.add(row);
            panel.add(Box.createVerticalStrut(5));
        }

        panel.add(Box.createVerticalGlue());
        JLabel ver = new JLabel("v1.0.0 — 2026");
        ver.setForeground(new Color(70, 75, 100));
        ver.setFont(new Font("Arial", Font.PLAIN, 10));
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(ver);

        return panel;
    }

    // ── LOGIN CARD ───────────────────────────────────────────────────────────
    private JPanel buildLoginCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(new Color(245, 246, 250));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 240), 1),
                BorderFactory.createEmptyBorder(36, 36, 36, 36)));
        card.setPreferredSize(new Dimension(390, 430));

        JLabel title = new JLabel("Bem-vindo de volta");
        title.setFont(new Font("Arial", Font.BOLD, 21));
        title.setForeground(new Color(22, 24, 35));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Escolha seu perfil de acesso");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(110, 115, 140));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title); card.add(Box.createVerticalStrut(4)); card.add(sub);
        card.add(Box.createVerticalStrut(28));

        // Role toggle
        JLabel roleLbl = new JLabel("PERFIL DE ACESSO");
        roleLbl.setFont(new Font("Arial", Font.BOLD, 9));
        roleLbl.setForeground(new Color(110, 115, 140));
        roleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(roleLbl);
        card.add(Box.createVerticalStrut(7));

        JPanel roleRow = new JPanel(new GridLayout(1, 2, 10, 0));
        roleRow.setBackground(Color.WHITE);
        roleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        roleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JToggleButton gerBtn = createRoleBtn("Gerente", "Acesso total");
        JToggleButton cliBtn = createRoleBtn("Cliente", "Meu histórico");
        ButtonGroup bg = new ButtonGroup();
        bg.add(gerBtn); bg.add(cliBtn);
        gerBtn.setSelected(true);
        roleRow.add(gerBtn); roleRow.add(cliBtn);
        card.add(roleRow);
        card.add(Box.createVerticalStrut(20));

        // CPF field (cliente only)
        JPanel cpfWrap = new JPanel();
        cpfWrap.setBackground(Color.WHITE);
        cpfWrap.setLayout(new BoxLayout(cpfWrap, BoxLayout.Y_AXIS));
        cpfWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        cpfWrap.setVisible(false);

        JLabel cpfLbl = new JLabel("CPF (somente números)");
        cpfLbl.setFont(new Font("Arial", Font.BOLD, 11));
        cpfLbl.setForeground(new Color(60, 65, 80));
        cpfLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField cpfField = styledField("000.000.000-00");
        cpfWrap.add(cpfLbl);
        cpfWrap.add(Box.createVerticalStrut(5));
        cpfWrap.add(cpfField);
        card.add(cpfWrap);

        // Toggle visibility
        ActionListener toggle = e -> {
            cpfWrap.setVisible(cliBtn.isSelected());
            card.revalidate(); card.repaint();
        };
        gerBtn.addActionListener(toggle);
        cliBtn.addActionListener(toggle);

        card.add(Box.createVerticalStrut(24));

        // Enter button
        JButton enterBtn = filledBtn("Entrar no sistema", new Color(0, 122, 255));
        enterBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        enterBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        enterBtn.addActionListener(e -> {
            if (gerBtn.isSelected()) {
                openManager();
            } else {
                String cpf = cpfField.getText().replaceAll("[^0-9]", "");
                if (cpf.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o CPF.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                openClient(cpf);
            }
        });
        card.add(enterBtn);
        card.add(Box.createVerticalStrut(14));

        // Register link
        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkRow.setBackground(Color.WHITE);
        linkRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel linkLbl = new JLabel("Novo por aqui?");
        linkLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        linkLbl.setForeground(new Color(110, 115, 140));
        JButton linkBtn = new JButton("Criar conta");
        linkBtn.setContentAreaFilled(false);
        linkBtn.setBorderPainted(false);
        linkBtn.setFocusPainted(false);
        linkBtn.setFont(new Font("Arial", Font.BOLD, 11));
        linkBtn.setForeground(new Color(0, 100, 230));
        linkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkBtn.addActionListener(e -> cardLayout.show(cardContainer, "register"));
        linkRow.add(linkLbl); linkRow.add(linkBtn);
        card.add(linkRow);

        outer.add(card);
        return outer;
    }

    // ── REGISTER CARD ────────────────────────────────────────────────────────
    private JPanel buildRegisterCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(new Color(245, 246, 250));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 240), 1),
                BorderFactory.createEmptyBorder(36, 36, 36, 36)));
        card.setPreferredSize(new Dimension(390, 460));

        JLabel title = new JLabel("Criar sua conta");
        title.setFont(new Font("Arial", Font.BOLD, 21));
        title.setForeground(new Color(22, 24, 35));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Preencha os dados para se cadastrar");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(110, 115, 140));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title); card.add(Box.createVerticalStrut(4)); card.add(sub);
        card.add(Box.createVerticalStrut(26));

        JLabel nameLbl = fieldLabel("Nome completo");
        JTextField nameField = styledField("Ex: Maria da Silva");
        JLabel cpfLbl = fieldLabel("CPF (somente números)");
        JTextField cpfField = styledField("00000000000");
        JLabel emailLbl = fieldLabel("E-mail");
        JTextField emailField = styledField("seu@email.com");

        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cpfLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        cpfField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLbl);
        card.add(nameField);
        card.add(Box.createVerticalStrut(12));
        card.add(cpfLbl);
        card.add(cpfField);
        card.add(Box.createVerticalStrut(12));
        card.add(emailLbl);
        card.add(emailField);

        card.add(Box.createVerticalStrut(24));

        JButton regBtn = filledBtn("Criar conta", new Color(76, 175, 80));
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        regBtn.addActionListener(e -> {
            String nome = nameField.getText().trim();
            String cpf = cpfField.getText().replaceAll("[^0-9]", "");
            String email = emailField.getText().trim();

            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cpf.length() != 11) {
                JOptionPane.showMessageDialog(this, "CPF deve ter 11 dígitos numéricos.", "CPF inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (controller.buscarClientePorCpf(cpf) != null) {
                JOptionPane.showMessageDialog(this, "CPF já cadastrado. Faça login.", "CPF existente", JOptionPane.WARNING_MESSAGE);
                cardLayout.show(cardContainer, "login");
                return;
            }
            try {
                controller.cadastrarCliente(nome, cpf, email);
                JOptionPane.showMessageDialog(this,
                        "Conta criada com sucesso! Bem-vindo(a), " + nome.split(" ")[0] + "!",
                        "Cadastro realizado", JOptionPane.INFORMATION_MESSAGE);
                // Auto-login
                model.Cliente novo = controller.buscarClientePorCpf(cpf);
                dispose();
                SwingUtilities.invokeLater(() -> new ClientView(controller, novo).setVisible(true));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        card.add(regBtn);
        card.add(Box.createVerticalStrut(14));

        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkRow.setBackground(Color.WHITE);
        linkRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel linkLbl = new JLabel("Já tem conta?");
        linkLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        linkLbl.setForeground(new Color(110, 115, 140));
        JButton backBtn = new JButton("Fazer login");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Arial", Font.BOLD, 11));
        backBtn.setForeground(new Color(0, 100, 230));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> cardLayout.show(cardContainer, "login"));
        linkRow.add(linkLbl); linkRow.add(backBtn);
        card.add(linkRow);

        outer.add(card);
        return outer;
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private JToggleButton createRoleBtn(String title, String subtitle) {
        JToggleButton btn = new JToggleButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = isSelected();
                g2.setColor(sel ? new Color(232, 242, 255) : new Color(249, 250, 253));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(sel ? new Color(0, 122, 255) : new Color(210, 214, 230));
                g2.setStroke(sel ? new BasicStroke(2f) : new BasicStroke(1f));
                g2.drawRoundRect(sel ? 1 : 0, sel ? 1 : 0,
                        sel ? getWidth() - 2 : getWidth() - 1,
                        sel ? getHeight() - 2 : getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 12));
        t.setForeground(new Color(22, 24, 35));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Arial", Font.PLAIN, 10));
        s.setForeground(new Color(110, 115, 140));
        s.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.add(Box.createVerticalGlue());
        btn.add(t); btn.add(Box.createVerticalStrut(2)); btn.add(s);
        btn.add(Box.createVerticalGlue());
        return btn;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        f.setForeground(new Color(22, 24, 35));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 230), 1),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return f;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(new Color(60, 65, 80));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    private JButton filledBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = bg;
                if (getModel().isPressed()) base = bg.darker();
                else if (getModel().isRollover()) base = bg.brighter();
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void openManager() {
        dispose();
        SwingUtilities.invokeLater(() -> new MainFrame(controller).setVisible(true));
    }

    private void openClient(String cpf) {
        model.Cliente c = controller.buscarClientePorCpf(cpf);
        if (c == null) {
            JOptionPane.showMessageDialog(this,
                    "CPF não encontrado. Crie uma conta primeiro.",
                    "Cliente não encontrado", JOptionPane.ERROR_MESSAGE);
            cardLayout.show(cardContainer, "register");
            return;
        }
        dispose();
        SwingUtilities.invokeLater(() -> new ClientView(controller, c).setVisible(true));
    }
}
