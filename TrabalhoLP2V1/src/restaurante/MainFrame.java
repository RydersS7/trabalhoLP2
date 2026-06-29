package restaurante;

import javax.swing.*;
import java.awt.*;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {
    private RestauranteController controller;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private String currentView = "dashboard";

    private DashboardPanel dashboardPanel;
    private OrderPanel orderPanel;
    private MenuPanel menuPanel;
    private ClientPanel clientPanel;

    // Sidebar stat labels for live update
    private JLabel livresVal;
    private JLabel ocupadasVal;
    private JLabel clientesVal;

    public MainFrame(RestauranteController controller) {
        this.controller = controller;
        setTitle("Fogo na Chapa — Churrascaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(new Color(245, 245, 245));
        sidebarPanel = createSidebar();
        bodyPanel.add(sidebarPanel, BorderLayout.WEST);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(245, 245, 245));

        dashboardPanel = new DashboardPanel(controller, this);
        orderPanel = new OrderPanel(controller, this);
        menuPanel = new MenuPanel(controller, this);
        clientPanel = new ClientPanel(controller, this);

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(orderPanel, "pedidos");
        contentPanel.add(menuPanel, "cardapio");
        contentPanel.add(clientPanel, "clientes");

        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(26, 26, 26));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 51, 51)));
        header.setPreferredSize(new Dimension(0, 60));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        leftPanel.setBackground(new Color(26, 26, 26));

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(0, 122, 255));
        logoPanel.setPreferredSize(new Dimension(28, 28));
        JLabel logoLabel = new JLabel("🍖");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        logoPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("Fogo na Chapa");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel subtitleLabel = new JLabel("Churrascaria");
        subtitleLabel.setForeground(new Color(136, 136, 136));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        leftPanel.add(logoPanel);
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        header.add(leftPanel, BorderLayout.WEST);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        statusPanel.setBackground(new Color(26, 26, 26));
        JPanel dotPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(76, 175, 80));
                g.fillOval(2, 2, 8, 8);
            }
        };
        dotPanel.setPreferredSize(new Dimension(12, 12));
        dotPanel.setBackground(new Color(26, 26, 26));
        JLabel statusLabel = new JLabel("Sistema ativo");
        statusLabel.setForeground(new Color(136, 136, 136));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton logoutBtn = new JButton("Sair");
        logoutBtn.setBackground(new Color(50, 52, 70));
        logoutBtn.setForeground(new Color(200, 205, 220));
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen(controller).setVisible(true));
        });

        statusPanel.add(dotPanel);
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(8));
        statusPanel.add(logoutBtn);
        header.add(statusPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(26, 26, 26));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(51, 51, 51)));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel navLabel = new JLabel("NAVEGAÇÃO");
        navLabel.setFont(new Font("Arial", Font.BOLD, 9));
        navLabel.setForeground(new Color(102, 102, 102));
        navLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navLabel);

        String[] navItems = {"dashboard", "pedidos", "cardapio", "clientes"};
        String[] navLabels = {"Dashboard", "Pedidos & Cozinha", "Cardápio", "Clientes"};

        for (int i = 0; i < navItems.length; i++) {
            final String viewId = navItems[i];
            JButton navBtn = createNavButton(navLabels[i], viewId.equals("dashboard"));
            navBtn.addActionListener(e -> switchView(viewId, navBtn));
            navButtons.add(navBtn);
            sidebar.add(navBtn);
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(26, 26, 26));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel resumoLabel = new JLabel("RESUMO");
        resumoLabel.setFont(new Font("Arial", Font.BOLD, 9));
        resumoLabel.setForeground(new Color(102, 102, 102));
        resumoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(resumoLabel);
        statsPanel.add(Box.createVerticalStrut(10));

        livresVal = new JLabel(String.valueOf(controller.contarMesasLivres()));
        livresVal.setForeground(new Color(76, 175, 80));
        livresVal.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(createStatItemWithRef("Mesas livres", livresVal));

        ocupadasVal = new JLabel(String.valueOf(controller.contarMesasOcupadas()));
        ocupadasVal.setForeground(new Color(244, 67, 54));
        ocupadasVal.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(createStatItemWithRef("Contas abertas", ocupadasVal));

        clientesVal = new JLabel(String.valueOf(controller.getListaClientes().size()));
        clientesVal.setForeground(new Color(0, 122, 255));
        clientesVal.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(createStatItemWithRef("Clientes", clientesVal));

        sidebar.add(statsPanel);
        return sidebar;
    }

    private JPanel createStatItemWithRef(String label, JLabel valueLabel) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(new Color(26, 26, 26));
        item.setMaximumSize(new Dimension(180, 30));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(new Color(136, 136, 136));
        labelComp.setFont(new Font("Arial", Font.PLAIN, 11));

        item.add(labelComp, BorderLayout.WEST);
        item.add(valueLabel, BorderLayout.EAST);
        item.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return item;
    }

    private JButton createNavButton(String label, boolean active) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setPreferredSize(new Dimension(200, 44));
        btn.setBackground(active ? new Color(0, 100, 210) : new Color(26, 26, 26));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color cur = btn.getBackground();
                if (!cur.equals(new Color(0, 100, 210))) btn.setBackground(new Color(45, 48, 70));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent evt) {
                Color cur = btn.getBackground();
                if (!cur.equals(new Color(0, 100, 210))) btn.setBackground(new Color(26, 26, 26));
            }
        });

        return btn;
    }

    private java.util.List<JButton> navButtons = new java.util.ArrayList<>();

    public void switchView(String viewId, JButton button) {
        currentView = viewId;
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, viewId);
        // Update button highlight
        for (JButton b : navButtons) {
            b.setBackground(new Color(26, 26, 26));
        }
        if (button != null) button.setBackground(new Color(0, 100, 210));
        if (viewId.equals("dashboard")) dashboardPanel.refresh();
        else if (viewId.equals("pedidos")) orderPanel.refresh();
        else if (viewId.equals("clientes")) clientPanel.refresh();
        else if (viewId.equals("cardapio")) menuPanel.refresh();
        refreshSidebar();
    }

    public void refreshSidebar() {
        if (livresVal != null) livresVal.setText(String.valueOf(controller.contarMesasLivres()));
        if (ocupadasVal != null) ocupadasVal.setText(String.valueOf(controller.contarMesasOcupadas()));
        if (clientesVal != null) clientesVal.setText(String.valueOf(controller.getListaClientes().size()));
        clientPanel.refresh();
    }

    public void refreshAllViews() {
        dashboardPanel.refresh();
        orderPanel.refresh();
        menuPanel.refresh();
        clientPanel.refresh();
        refreshSidebar();
    }

    public RestauranteController getController() { return controller; }
}
