package restaurante;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame - Interface principal do sistema MEZA
 * Arquitetura MVC integrada com atualização automática
 */
public class MainFrame extends JFrame {
    private RestauranteController controller;
    private JPanel contentPanel;
    private String currentView = "dashboard";
    
    private DashboardPanel dashboardPanel;
    private OrderPanel orderPanel;
    private MenuPanel menuPanel;
    private ClientPanel clientPanel;

    public MainFrame(RestauranteController controller) {
        this.controller = controller;
        
        setTitle("MEZA - Gestão de Restaurante");
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
        bodyPanel.add(createSidebar(), BorderLayout.WEST);
        
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
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        JLabel logoLabel = new JLabel("⊞");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        logoPanel.add(logoLabel);
        
        JLabel titleLabel = new JLabel("MEZA");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 15));
        
        JLabel subtitleLabel = new JLabel("Gestão de Restaurante");
        subtitleLabel.setForeground(new Color(136, 136, 136));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        leftPanel.add(logoPanel);
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        
        header.add(leftPanel, BorderLayout.WEST);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        statusPanel.setBackground(new Color(26, 26, 26));
        
        JPanel dotPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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
        
        statusPanel.add(dotPanel);
        statusPanel.add(statusLabel);
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
        String[] navLabels = {"Dashboard", "Pedidos Ativos", "Cardápio", "Clientes"};

        for (int i = 0; i < navItems.length; i++) {
            final String viewId = navItems[i];
            JButton navBtn = createNavButton(navLabels[i], viewId.equals("dashboard"));
            navBtn.addActionListener(e -> switchView(viewId, navBtn));
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

        int livres = controller.contarMesasLivres();
        int ocupadas = controller.contarMesasOcupadas();
        int clientes = controller.getListaClientes().size();

        statsPanel.add(createStatItem("Mesas livres", String.valueOf(livres), new Color(76, 175, 80)));
        statsPanel.add(createStatItem("Contas abertas", String.valueOf(ocupadas), new Color(244, 67, 54)));
        statsPanel.add(createStatItem("Clientes", String.valueOf(clientes), new Color(0, 122, 255)));

        sidebar.add(statsPanel);
        return sidebar;
    }

    private JButton createNavButton(String label, boolean active) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setPreferredSize(new Dimension(200, 44));
        btn.setBackground(active ? new Color(58, 58, 58) : new Color(26, 26, 26));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 0, 0, 0));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(new Color(58, 58, 58))) {
                    btn.setBackground(new Color(45, 45, 45));
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(new Color(58, 58, 58))) {
                    btn.setBackground(new Color(26, 26, 26));
                }
            }
        });
        
        return btn;
    }

    private JPanel createStatItem(String label, String value, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(new Color(26, 26, 26));
        item.setMaximumSize(new Dimension(180, 30));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(new Color(136, 136, 136));
        labelComp.setFont(new Font("Arial", Font.PLAIN, 11));

        JLabel valueComp = new JLabel(value);
        valueComp.setForeground(color);
        valueComp.setFont(new Font("JetBrains Mono", Font.BOLD, 12));

        item.add(labelComp, BorderLayout.WEST);
        item.add(valueComp, BorderLayout.EAST);
        item.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        return item;
    }

    public void switchView(String viewId, JButton button) {
        currentView = viewId;
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, viewId);
        
        if (viewId.equals("dashboard")) {
            dashboardPanel.refresh();
        } else if (viewId.equals("pedidos")) {
            orderPanel.refresh();
        }
    }

    public void refreshAllViews() {
        dashboardPanel.refresh();
        orderPanel.refresh();
        menuPanel.refresh();
        clientPanel.refresh();
    }

    public RestauranteController getController() {
        return controller;
    }
}