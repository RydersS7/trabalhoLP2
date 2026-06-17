package view;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 * MEZA - Sistema de Gestão de Restaurante
 * MainFrame.java — Java Swing / GroupLayout (padrão NetBeans)
 *
 * Arquitetura:
 *   MainFrame
 *    ├─ RoundedPanel        (classe interna estática)
 *    ├─ buildSidebar()
 *    ├─ buildTopbar()
 *    ├─ CardLayout
 *    │   ├─ DashboardPanel
 *    │   ├─ OrderPanel      (detalhe de pedido)
 *    │   ├─ MenuPanel       (cardápio)
 *    │   └─ CustomersPanel  (clientes)
 *    └─ métodos auxiliares
 */
public class MainFrame_V1 extends JFrame {

    // ─── Paleta ────────────────────────────────────────────────────────────────
    static final Color BG          = new Color(0xF5F2EE);
    static final Color SIDEBAR_BG  = new Color(0xFFFFFF);
    static final Color CARD_BG     = new Color(0xFFFFFF);
    static final Color AMBER       = new Color(0xC07830);   // laranja/amber primário
    static final Color AMBER_LIGHT = new Color(0xFAF0E4);   // fundo hover sidebar
    static final Color GREEN       = new Color(0x4CAF50);
    static final Color GREEN_LIGHT = new Color(0xE8F5E9);
    static final Color RED         = new Color(0xE53935);
    static final Color RED_LIGHT   = new Color(0xFFEBEE);
    static final Color TEXT_DARK   = new Color(0x1A1A1A);
    static final Color TEXT_MID    = new Color(0x6B6B6B);
    static final Color TEXT_LIGHT  = new Color(0xA0A0A0);
    static final Color DIVIDER     = new Color(0xEAE7E2);
    static final Color TOPBAR_BG   = new Color(0xFFFFFF);

    // ─── Layout ────────────────────────────────────────────────────────────────
    static final int SIDEBAR_W = 224;
    static final int TOPBAR_H  = 56;

    // ─── Estado / navegação ────────────────────────────────────────────────────
    CardLayout cardLayout;
    JPanel     contentArea;
    String     currentView = "dashboard";

    // Botões sidebar (para destacar ativo)
    JButton btnMesas, btnCardapio, btnClientes;

    // Label topbar
    JLabel lblTopTitle, lblTopDate;

    // ─── Dados de demo ─────────────────────────────────────────────────────────
    record MesaInfo(int num, String status, String cliente, String tempo) {}
    record PedidoItem(String nome, int qtd, double preco) {}
    record ClienteInfo(String nome, String cpf, String email,
                       double bonus, double totalGasto, int visitas) {}

    List<MesaInfo> mesas = new ArrayList<>(List.of(
        new MesaInfo(1,  "LIVRE",   "",             ""),
        new MesaInfo(2,  "OCUPADA", "Jo\u00e3o Silva",    "1h 23m"),
        new MesaInfo(3,  "LIVRE",   "",             ""),
        new MesaInfo(4,  "OCUPADA", "Maria Fernanda","45m"),
        new MesaInfo(5,  "OCUPADA", "Carlos Mendes", "2h 07m"),
        new MesaInfo(6,  "LIVRE",   "",             ""),
        new MesaInfo(7,  "OCUPADA", "Ana Beatriz",  "32m"),
        new MesaInfo(8,  "LIVRE",   "",             ""),
        new MesaInfo(9,  "OCUPADA", "Pedro Alves",  "1h 12m"),
        new MesaInfo(10, "LIVRE",   "",             "")
    ));

    List<PedidoItem> pedidoDemo = List.of(
        new PedidoItem("Risoto de Funghi",        2, 48.00),
        new PedidoItem("Fil\u00e9 ao Molho Madeira",  1, 62.00),
        new PedidoItem("Salada Caprese",           1, 28.00),
        new PedidoItem("\u00c1gua Mineral 500ml",  3,  6.00),
        new PedidoItem("Suco de Laranja",          2, 14.00)
    );

    record MenuItemInfo(String nome, String desc, double preco, boolean isBebida,
                        String extra) {}
    List<MenuItemInfo> menuComidas = List.of(
        new MenuItemInfo("Risoto de Funghi",       "Arroz arb\u00f3reo, shiitake e porcini, parmesão ralado",    48.0, false, ""),
        new MenuItemInfo("Fil\u00e9 ao Molho Madeira","Contra-fil\u00e9 grelhado, molho madeira, batata r\u00fastica", 62.0, false, ""),
        new MenuItemInfo("P\u00e3o de Alho",       "P\u00e3o artesanal, manteiga de alho, ervas finas",          12.0, false, ""),
        new MenuItemInfo("Salada Caprese",          "Tomate, mussarela de b\u00fafala, manjeric\u00e3o, azeite",  28.0, false, ""),
        new MenuItemInfo("Massa ao Pesto",          "Espaguete, pesto de manjeric\u00e3o, parmesão, pinoli",       38.0, false, ""),
        new MenuItemInfo("Bruschetta Cl\u00e1ssica","Ciabatta, tomate, alho, manjeric\u00e3o, azeite extra-virgem",22.0, false, "")
    );
    List<MenuItemInfo> menuBebidas = List.of(
        new MenuItemInfo("\u00c1gua Mineral",       "Fornecedor: Crystal",   6.0,  true, "500ml"),
        new MenuItemInfo("Suco de Laranja",         "Natural, sem a\u00e7\u00facar",      14.0, true, "300ml"),
        new MenuItemInfo("Refrigerante Lata",       "Coca-Cola, Guaraná",    8.0,  true, "350ml"),
        new MenuItemInfo("Cerveja Artesanal",        "IPA, Pilsen ou Weiss",  18.0, true, "600ml"),
        new MenuItemInfo("Vinho Tinto Ta\u00e7a",   "Cabernet Sauvignon",    22.0, true, "150ml"),
        new MenuItemInfo("Caf\u00e9 Expresso",      "Blend especial da casa",  7.0, true, "50ml")
    );

    List<ClienteInfo> clientes = List.of(
        new ClienteInfo("Jo\u00e3o Silva",     "***.456.789-**", "joao.silva@gmail.com",     47.50,  695.00, 5),
        new ClienteInfo("Maria Fernanda", "***.123.456-**", "maria.f@hotmail.com",       31.20,  440.00, 3),
        new ClienteInfo("Carlos Mendes",  "***.789.012-**", "cmendes@empresa.com.br",    18.80,  210.00, 2),
        new ClienteInfo("Ana Beatriz",    "***.345.678-**", "ana.b@gmail.com",           62.10,  875.00, 7),
        new ClienteInfo("Pedro Alves",    "***.901.234-**", "pedro.alves@outlook.com",    9.50,  128.00, 1)
    );

    // ───────────────────────────────────────────────────────────────────────────
    public MainFrame_V1() {
        setTitle("MEZA \u2014 Gest\u00e3o de Restaurante");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        buildUI();
        setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BUILD UI PRINCIPAL
    // ═══════════════════════════════════════════════════════════════════════════
    void buildUI() {
        JPanel root = new JPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.setBackground(BG);

        // Sidebar
        JPanel sidebar = buildSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // Coluna direita (topbar + conteúdo)
        JPanel rightCol = new JPanel(new BorderLayout(0, 0));
        rightCol.setBackground(BG);

        JPanel topbar = buildTopbar();
        rightCol.add(topbar, BorderLayout.NORTH);

        // CardLayout para as views
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(BG);
        contentArea.add(buildDashboardPanel(), "dashboard");
        contentArea.add(buildMenuPanel(),      "cardapio");
        contentArea.add(buildCustomersPanel(), "clientes");

        rightCol.add(contentArea, BorderLayout.CENTER);
        root.add(rightCol, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ═══════════════════════════════════════════════════════════════════════════
    JPanel buildSidebar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        panel.setBackground(SIDEBAR_BG);
        panel.setLayout(new GroupLayout(panel));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DIVIDER));

        GroupLayout gl = (GroupLayout) panel.getLayout();
        gl.setAutoCreateGaps(false);
        gl.setAutoCreateContainerGaps(false);

        // ── Logo ──
        JLabel lblLogo = new JLabel("MEZA");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 22));
        lblLogo.setForeground(AMBER);
        JLabel lblSlogan = new JLabel("Gest\u00e3o que flui.");
        lblSlogan.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSlogan.setForeground(TEXT_LIGHT);

        // ── Navegação ──
        btnMesas    = navButton("\u229e  Mesas",    "dashboard");
        btnCardapio = navButton("\u25a4  Card\u00e1pio", "cardapio");
        btnClientes = navButton("\u25e6  Clientes", "clientes");

        // badge mesas
        JLabel badge = new JLabel("5");
        badge.setFont(new Font("Arial", Font.BOLD, 10));
        badge.setForeground(Color.WHITE);
        badge.setBackground(AMBER);
        badge.setOpaque(true);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setPreferredSize(new Dimension(20, 20));
        badge.setBorder(new EmptyBorder(0,0,0,0));

        // badge sobre o botão mesas — uso JLayeredPane
        JLayeredPane mesaRow = new JLayeredPane();
        mesaRow.setPreferredSize(new Dimension(SIDEBAR_W - 24, 40));
        mesaRow.setOpaque(false);
        btnMesas.setBounds(0, 0, SIDEBAR_W - 24, 40);
        badge.setBounds(SIDEBAR_W - 24 - 28, 10, 22, 20);
        mesaRow.add(btnMesas, JLayeredPane.DEFAULT_LAYER);
        mesaRow.add(badge,    JLayeredPane.PALETTE_LAYER);

        // ── Usuário (rodapé) ──
        JPanel userPanel = buildUserPanel();

        // ── Separador ──
        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER);

        // ── GroupLayout ──
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addGap(20).addComponent(lblLogo))
            .addGroup(gl.createSequentialGroup()
                .addGap(20).addComponent(lblSlogan))
            .addGroup(gl.createSequentialGroup()
                .addGap(12).addComponent(mesaRow))
            .addGroup(gl.createSequentialGroup()
                .addGap(12).addComponent(btnCardapio, SIDEBAR_W - 24, SIDEBAR_W - 24, SIDEBAR_W - 24))
            .addGroup(gl.createSequentialGroup()
                .addGap(12).addComponent(btnClientes, SIDEBAR_W - 24, SIDEBAR_W - 24, SIDEBAR_W - 24))
            .addComponent(sep)
            .addComponent(userPanel)
        );
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGap(24)
            .addComponent(lblLogo)
            .addGap(4)
            .addComponent(lblSlogan)
            .addGap(32)
            .addComponent(mesaRow, 40, 40, 40)
            .addGap(4)
            .addComponent(btnCardapio, 40, 40, 40)
            .addGap(4)
            .addComponent(btnClientes, 40, 40, 40)
            .addGap(Short.MAX_VALUE)
            .addComponent(sep, 1, 1, 1)
            .addComponent(userPanel, 64, 64, 64)
        );

        setNavActive("dashboard");
        return panel;
    }

    JButton navButton(String text, String view) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getBackground().equals(AMBER_LIGHT)) {
                    g2.setColor(AMBER_LIGHT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(TEXT_MID);
        btn.setBackground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 12, 0, 0));
        btn.addActionListener(e -> navigate(view));
        return btn;
    }

    void setNavActive(String view) {
        for (JButton b : new JButton[]{btnMesas, btnCardapio, btnClientes}) {
            b.setForeground(TEXT_MID);
            b.setFont(new Font("Arial", Font.PLAIN, 13));
            b.setBackground(Color.WHITE);
        }
        JButton active = switch (view) {
            case "cardapio"  -> btnCardapio;
            case "clientes"  -> btnClientes;
            default          -> btnMesas;
        };
        active.setForeground(AMBER);
        active.setFont(new Font("Arial", Font.BOLD, 13));
        active.setBackground(AMBER_LIGHT);
    }

    JPanel buildUserPanel() {
        JPanel p = new JPanel();
        p.setBackground(SIDEBAR_BG);
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 14));

        // Avatar
        JLabel avatar = new JLabel("AL") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMBER);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t,
                    (getWidth()  - fm.stringWidth(t)) / 2,
                    (getHeight() + fm.getAscent()  - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setOpaque(false);

        JPanel info = new JPanel();
        info.setBackground(SIDEBAR_BG);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Andr\u00e9 Lima");
        name.setFont(new Font("Arial", Font.BOLD, 12));
        name.setForeground(TEXT_DARK);
        JLabel role = new JLabel("Gerente");
        role.setFont(new Font("Arial", Font.PLAIN, 11));
        role.setForeground(TEXT_LIGHT);
        info.add(name);
        info.add(role);

        p.add(avatar);
        p.add(info);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TOPBAR
    // ═══════════════════════════════════════════════════════════════════════════
    JPanel buildTopbar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(0, TOPBAR_H));
        panel.setBackground(TOPBAR_BG);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));

        GroupLayout gl = new GroupLayout(panel);
        panel.setLayout(gl);
        gl.setAutoCreateGaps(false);

        lblTopTitle = new JLabel("Vis\u00e3o Geral");
        lblTopTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTopTitle.setForeground(TEXT_DARK);

        lblTopDate = new JLabel(getDateString());
        lblTopDate.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTopDate.setForeground(TEXT_LIGHT);

        JTextField search = new JTextField("Pesquisar...");
        search.setFont(new Font("Arial", Font.PLAIN, 12));
        search.setForeground(TEXT_LIGHT);
        search.setBackground(BG);
        search.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(DIVIDER, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        search.setPreferredSize(new Dimension(180, 30));
        search.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (search.getText().equals("Pesquisar...")) { search.setText(""); search.setForeground(TEXT_DARK); } }
            public void focusLost(FocusEvent e)   { if (search.getText().isEmpty()) { search.setText("Pesquisar..."); search.setForeground(TEXT_LIGHT); } }
        });

        JLabel bell = new JLabel("\uD83D\uDD14");
        bell.setFont(new Font("Arial", Font.PLAIN, 16));
        bell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel titleCol = new JPanel();
        titleCol.setOpaque(false);
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.add(lblTopTitle);
        titleCol.add(lblTopDate);

        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGap(24)
            .addComponent(titleCol)
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(search, 180, 180, 180)
            .addGap(16)
            .addComponent(bell)
            .addGap(20)
        );
        gl.setVerticalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(titleCol)
            .addComponent(search, 30, 30, 30)
            .addComponent(bell)
        );
        return panel;
    }

    String getDateString() {
        String[] dias  = {"Domingo","Segunda-feira","Ter\u00e7a-feira","Quarta-feira","Quinta-feira","Sexta-feira","S\u00e1bado"};
        String[] meses = {"janeiro","fevereiro","mar\u00e7o","abril","maio","junho","julho","agosto","setembro","outubro","novembro","dezembro"};
        Calendar c = Calendar.getInstance();
        return dias[c.get(Calendar.DAY_OF_WEEK)-1] + ", " +
               c.get(Calendar.DAY_OF_MONTH) + " de " +
               meses[c.get(Calendar.MONTH)] + " de " +
               c.get(Calendar.YEAR);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NAVEGAÇÃO
    // ═══════════════════════════════════════════════════════════════════════════
    void navigate(String view) {
        currentView = view;
        cardLayout.show(contentArea, view);
        setNavActive(view);
        lblTopTitle.setText(switch (view) {
            case "cardapio" -> "Card\u00e1pio";
            case "clientes" -> "Clientes";
            default         -> "Vis\u00e3o Geral";
        });
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DASHBOARD PANEL  (mesas + sumário)
    // ═══════════════════════════════════════════════════════════════════════════
    JPanel buildDashboardPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // ── Linha de KPIs ──
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiRow.setOpaque(false);
        kpiRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        kpiRow.add(buildKpiCard("Mesas ocupadas",  "5/10", "Em atendimento",          null));
        kpiRow.add(buildKpiCard("Mesas livres",    "5/10", "Dispon\u00edveis agora",  null));
        kpiRow.add(buildKpiCard("Faturamento hoje","R$ 1.251,50","Pedidos abertos + fechados", null));
        kpiRow.add(buildKpiCard("Tempo m\u00e9dio","1h 02m","Por mesa ocupada",       null));

        content.add(kpiRow);
        content.add(Box.createVerticalStrut(20));

        // ── Título + legenda ──
        JPanel mesasHeader = new JPanel(new BorderLayout());
        mesasHeader.setOpaque(false);
        mesasHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lblMesas = new JLabel("Mesas");
        lblMesas.setFont(new Font("Arial", Font.BOLD, 15));
        lblMesas.setForeground(TEXT_DARK);

        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        legenda.setOpaque(false);
        legenda.add(colorDot(GREEN)); legenda.add(quickLabel("Livre",   TEXT_MID));
        legenda.add(colorDot(RED));   legenda.add(quickLabel("Ocupada", TEXT_MID));

        mesasHeader.add(lblMesas, BorderLayout.WEST);
        mesasHeader.add(legenda,  BorderLayout.EAST);

        content.add(mesasHeader);
        content.add(Box.createVerticalStrut(12));

        // ── Grid 5×2 ──
        JPanel grid = new JPanel(new GridLayout(2, 5, 12, 12));
        grid.setOpaque(false);
        for (MesaInfo m : mesas) {
            grid.add(buildMesaCard(m));
        }
        content.add(grid);

        outer.add(content, BorderLayout.CENTER);
        return outer;
    }

    JPanel buildKpiCard(String titulo, String valor, String subtitulo, Color accent) {
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lTit = quickLabel(titulo, TEXT_LIGHT);
        lTit.setFont(new Font("Arial", Font.PLAIN, 11));

        JLabel lVal = new JLabel(valor);
        lVal.setFont(new Font("Arial", Font.BOLD, accent != null ? 20 : 22));
        lVal.setForeground(accent != null ? accent : TEXT_DARK);

        JLabel lSub = quickLabel(subtitulo, TEXT_LIGHT);
        lSub.setFont(new Font("Arial", Font.PLAIN, 11));

        card.add(lTit);
        card.add(Box.createVerticalStrut(6));
        card.add(lVal);
        card.add(Box.createVerticalStrut(4));
        card.add(lSub);
        return card;
    }

    JPanel buildMesaCard(MesaInfo m) {
        boolean ocupada = "OCUPADA".equals(m.status());
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.setLayout(new GroupLayout(card));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GroupLayout gl = (GroupLayout) card.getLayout();
        gl.setAutoCreateGaps(false);

        // Número
        JLabel num = new JLabel(String.format("%02d", m.num()));
        num.setFont(new Font("Arial", Font.BOLD, ocupada ? 22 : 26));
        num.setForeground(ocupada ? RED : TEXT_DARK);

        // Badge status
        JLabel status = new JLabel(m.status());
        status.setFont(new Font("Arial", Font.BOLD, 9));
        status.setForeground(ocupada ? RED : GREEN);
        status.setBackground(ocupada ? RED_LIGHT : GREEN_LIGHT);
        status.setOpaque(true);
        status.setBorder(new EmptyBorder(2, 6, 2, 6));

        // Cliente / tempo
        JLabel cliente = new JLabel(ocupada ? m.cliente() : "Dispon\u00edvel");
        cliente.setFont(new Font("Arial", Font.PLAIN, 12));
        cliente.setForeground(ocupada ? TEXT_DARK : TEXT_LIGHT);

        JLabel tempo = new JLabel(ocupada ? "\u23f1 " + m.tempo() : "");
        tempo.setFont(new Font("Arial", Font.PLAIN, 11));
        tempo.setForeground(TEXT_LIGHT);

        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(num)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(status))
            .addComponent(cliente)
            .addComponent(tempo)
        );
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(num)
                .addComponent(status))
            .addGap(8)
            .addComponent(cliente)
            .addGap(2)
            .addComponent(tempo)
        );

        // Hover + click para detalhe
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(0xFAF8F5)); card.repaint(); }
            public void mouseExited(MouseEvent e)  { card.setBackground(CARD_BG); card.repaint(); }
            public void mouseClicked(MouseEvent e) {
                if (ocupada) showOrderDetail(m);
            }
        });
        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ORDER DETAIL  (diálogo modal 3 colunas)
    // ═══════════════════════════════════════════════════════════════════════════
    void showOrderDetail(MesaInfo m) {
        JDialog dlg = new JDialog(this, "Mesa " + String.format("%02d", m.num()) + " \u2014 " + m.cliente(), true);
        dlg.setSize(860, 560);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(BG);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ── 3 colunas ──
        JPanel cols = new JPanel(new GridLayout(1, 3, 14, 0));
        cols.setOpaque(false);

        // Coluna 1 – info da mesa
        cols.add(buildOrderInfoCol(m));
        // Coluna 2 – itens do pedido
        cols.add(buildOrderItemsCol());
        // Coluna 3 – pagamento
        cols.add(buildOrderPayCol(m));

        root.add(cols, BorderLayout.CENTER);
        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    JPanel buildOrderInfoCol(MesaInfo m) {
        RoundedPanel p = new RoundedPanel(12);
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(sectionTitle("Informa\u00e7\u00f5es"));
        p.add(Box.createVerticalStrut(14));
        p.add(infoRow("\uD83D\uDCCB", "Mesa",    String.format("%02d", m.num())));
        p.add(Box.createVerticalStrut(8));
        p.add(infoRow("\uD83D\uDC64", "Cliente", m.cliente()));
        p.add(Box.createVerticalStrut(8));
        p.add(infoRow("\u23F1", "Tempo",    m.tempo()));
        p.add(Box.createVerticalStrut(8));
        p.add(infoRow("\uD83D\uDD34", "Status",   "Ocupada"));
        p.add(Box.createVerticalStrut(18));
        p.add(new JSeparator());
        p.add(Box.createVerticalStrut(14));
        p.add(sectionTitle("Resumo"));
        p.add(Box.createVerticalStrut(10));

        double total = pedidoDemo.stream().mapToDouble(i -> i.preco() * i.qtd()).sum();
        p.add(infoRow("", "Subtotal",  "R$ " + fmt(total)));
        p.add(Box.createVerticalStrut(6));
        p.add(infoRow("", "Taxa serv.", "R$ " + fmt(total * 0.1)));
        p.add(Box.createVerticalStrut(6));

        JLabel totalLbl = new JLabel("Total: R$ " + fmt(total * 1.1));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 14));
        totalLbl.setForeground(AMBER);
        totalLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(totalLbl);
        return p;
    }

    JPanel buildOrderItemsCol() {
        RoundedPanel p = new RoundedPanel(12);
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        p.setLayout(new BorderLayout(0, 0));

        JLabel title = sectionTitle("Itens do Pedido");
        p.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(12, 0, 0, 0));

        for (PedidoItem item : pedidoDemo) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            row.setBorder(new EmptyBorder(4, 0, 4, 0));

            JLabel qtd = new JLabel(item.qtd() + "x");
            qtd.setFont(new Font("Arial", Font.BOLD, 12));
            qtd.setForeground(AMBER);
            qtd.setPreferredSize(new Dimension(28, 20));

            JLabel nome = new JLabel(item.nome());
            nome.setFont(new Font("Arial", Font.PLAIN, 12));
            nome.setForeground(TEXT_DARK);

            JLabel preco = new JLabel("R$ " + fmt(item.preco() * item.qtd()));
            preco.setFont(new Font("Arial", Font.PLAIN, 12));
            preco.setForeground(TEXT_MID);

            row.add(qtd,   BorderLayout.WEST);
            row.add(nome,  BorderLayout.CENTER);
            row.add(preco, BorderLayout.EAST);
            list.add(row);
            list.add(new JSeparator());
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    JPanel buildOrderPayCol(MesaInfo m) {
        RoundedPanel p = new RoundedPanel(12);
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(sectionTitle("Pagamento"));
        p.add(Box.createVerticalStrut(14));

        String[] metodos = {"Dinheiro", "Cart\u00e3o D\u00e9bito", "Cart\u00e3o Cr\u00e9dito", "Pix"};
        ButtonGroup bg = new ButtonGroup();
        for (String met : metodos) {
            JRadioButton rb = new JRadioButton(met);
            rb.setFont(new Font("Arial", Font.PLAIN, 13));
            rb.setForeground(TEXT_DARK);
            rb.setOpaque(false);
            rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            bg.add(rb);
            p.add(rb);
            p.add(Box.createVerticalStrut(6));
        }

        p.add(Box.createVerticalStrut(12));
        p.add(new JSeparator());
        p.add(Box.createVerticalStrut(12));

        JCheckBox useBonus = new JCheckBox("Usar saldo b\u00f4nus");
        useBonus.setFont(new Font("Arial", Font.PLAIN, 12));
        useBonus.setOpaque(false);
        useBonus.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(useBonus);
        p.add(Box.createVerticalStrut(18));

        JButton btnPagar = roundedButton("Confirmar Pagamento", AMBER, Color.WHITE);
        btnPagar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPagar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnPagar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Pagamento da Mesa " + String.format("%02d", m.num()) + " confirmado!",
                "MEZA", JOptionPane.INFORMATION_MESSAGE);
            Window w = SwingUtilities.getWindowAncestor(btnPagar);
            if (w != null) w.dispose();
        });
        p.add(btnPagar);
        p.add(Box.createVerticalStrut(10));

        JButton btnFechar = roundedButton("Fechar sem pagar", DIVIDER, TEXT_MID);
        btnFechar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnFechar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnFechar.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(btnFechar);
            if (w != null) w.dispose();
        });
        p.add(btnFechar);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MENU PANEL
    // ═══════════════════════════════════════════════════════════════════════════
    JPanel buildMenuPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // Tabs
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabs.setOpaque(false);
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton tabComidas  = tabButton("Comidas",  true);
        JButton tabBebidas  = tabButton("Bebidas",  false);

        // Grid panel (troca entre comidas / bebidas)
        JPanel gridHolder = new JPanel(new CardLayout());
        gridHolder.setOpaque(false);
        gridHolder.add(buildMenuGrid(menuComidas),  "comidas");
        gridHolder.add(buildMenuGrid(menuBebidas),  "bebidas");

        CardLayout gl2 = (CardLayout) gridHolder.getLayout();

        tabComidas.addActionListener(e -> {
            tabComidas.setBackground(Color.WHITE);
            tabComidas.setForeground(TEXT_DARK);
            tabBebidas.setBackground(BG);
            tabBebidas.setForeground(TEXT_MID);
            gl2.show(gridHolder, "comidas");
        });
        tabBebidas.addActionListener(e -> {
            tabBebidas.setBackground(Color.WHITE);
            tabBebidas.setForeground(TEXT_DARK);
            tabComidas.setBackground(BG);
            tabComidas.setForeground(TEXT_MID);
            gl2.show(gridHolder, "bebidas");
        });

        tabs.add(tabComidas);
        tabs.add(tabBebidas);

        inner.add(tabs);
        inner.add(Box.createVerticalStrut(16));
        inner.add(gridHolder);

        // FAB
        JButton fab = new JButton("+") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMBER);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("+",
                    (getWidth()  - fm.stringWidth("+")) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        fab.setPreferredSize(new Dimension(46, 46));
        fab.setBorderPainted(false);
        fab.setContentAreaFilled(false);
        fab.setFocusPainted(false);
        fab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fab.setToolTipText("Adicionar item ao card\u00e1pio");

        JPanel fabWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        fabWrap.setOpaque(false);
        fabWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        fabWrap.add(fab);
        inner.add(fabWrap);

        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    JPanel buildMenuGrid(List<MenuItemInfo> items) {
        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setOpaque(false);
        for (MenuItemInfo it : items) {
            grid.add(buildMenuItemCard(it));
        }
        return grid;
    }

    JPanel buildMenuItemCard(MenuItemInfo it) {
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setLayout(new GroupLayout(card));

        GroupLayout gl = (GroupLayout) card.getLayout();
        gl.setAutoCreateGaps(false);

        // Ícone
        JLabel icon = new JLabel(it.isBebida() ? "\uD83E\uDD64" : "\uD83C\uDF7D") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF5F0E8));
                g2.fillOval(0, 0, 36, 36);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setPreferredSize(new Dimension(36, 36));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel preco = new JLabel("R$ " + fmt(it.preco()));
        preco.setFont(new Font("Arial", Font.BOLD, 13));
        preco.setForeground(AMBER);

        JLabel nome = new JLabel(it.nome());
        nome.setFont(new Font("Arial", Font.BOLD, 13));
        nome.setForeground(TEXT_DARK);

        JLabel desc = new JLabel("<html><div style='width:140px'>" + it.desc() + (it.extra().isEmpty() ? "" : " · " + it.extra()) + "</div></html>");
        desc.setFont(new Font("Arial", Font.PLAIN, 11));
        desc.setForeground(TEXT_LIGHT);

        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(icon, 36, 36, 36)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(preco))
            .addComponent(nome)
            .addComponent(desc)
        );
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(icon, 36, 36, 36)
                .addComponent(preco))
            .addGap(10)
            .addComponent(nome)
            .addGap(4)
            .addComponent(desc)
        );

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(0xFAF8F5)); card.repaint(); }
            public void mouseExited(MouseEvent e)  { card.setBackground(CARD_BG); card.repaint(); }
        });
        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CUSTOMERS PANEL
    // ═══════════════════════════════════════════════════════════════════════════
    JPanel buildCustomersPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Lista de clientes (esquerda) + detalhe (direita)
        JPanel split = new JPanel(new GridLayout(1, 2, 14, 0));
        split.setOpaque(false);

        // Lista
        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JLabel lTitle = new JLabel("Clientes");
        lTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lTitle.setForeground(TEXT_DARK);
        lTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.add(lTitle);
        listPanel.add(Box.createVerticalStrut(12));

        // Painel de detalhe (referência para atualizar)
        JPanel[] detailHolder = {buildCustomerDetail(clientes.get(0))};
        JPanel detailWrap = new JPanel(new BorderLayout());
        detailWrap.setOpaque(false);
        detailWrap.add(detailHolder[0], BorderLayout.CENTER);

        for (ClienteInfo c : clientes) {
            JPanel row = buildCustomerRow(c);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    detailWrap.removeAll();
                    detailWrap.add(buildCustomerDetail(c), BorderLayout.CENTER);
                    detailWrap.revalidate();
                    detailWrap.repaint();
                }
                public void mouseEntered(MouseEvent e) { row.setBackground(new Color(0xFAF8F5)); }
                public void mouseExited(MouseEvent e)  { row.setBackground(CARD_BG); }
            });
            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(8));
        }

        split.add(listPanel);
        split.add(detailWrap);

        outer.add(split, BorderLayout.CENTER);
        return outer;
    }

    JPanel buildCustomerRow(ClienteInfo c) {
        RoundedPanel row = new RoundedPanel(10);
        row.setBackground(CARD_BG);
        row.setBorder(new EmptyBorder(12, 14, 12, 14));
        row.setLayout(new BorderLayout(12, 0));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Avatar
        String initials = Arrays.stream(c.nome().split(" "))
            .limit(2).map(s -> s.substring(0,1))
            .reduce("", String::concat);
        JLabel avatar = makeAvatar(initials, 38);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel(c.nome());
        nome.setFont(new Font("Arial", Font.BOLD, 13));
        nome.setForeground(TEXT_DARK);
        JLabel email = new JLabel(c.email());
        email.setFont(new Font("Arial", Font.PLAIN, 11));
        email.setForeground(TEXT_LIGHT);
        info.add(nome);
        info.add(email);

        JLabel bonus = new JLabel("R$ " + fmt(c.bonus()));
        bonus.setFont(new Font("Arial", Font.BOLD, 12));
        bonus.setForeground(AMBER);

        row.add(avatar, BorderLayout.WEST);
        row.add(info,   BorderLayout.CENTER);
        row.add(bonus,  BorderLayout.EAST);
        return row;
    }

    JPanel buildCustomerDetail(ClienteInfo c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        // ── Card principal ──
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setLayout(new BorderLayout(16, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String initials = Arrays.stream(c.nome().split(" "))
            .limit(2).map(s -> s.substring(0,1))
            .reduce("", String::concat);
        JLabel av = makeAvatar(initials, 52);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nameRow.setOpaque(false);
        JLabel nome = new JLabel(c.nome());
        nome.setFont(new Font("Arial", Font.BOLD, 16));
        nome.setForeground(TEXT_DARK);
        JLabel fiel = new JLabel("Cliente fiel");
        fiel.setFont(new Font("Arial", Font.BOLD, 10));
        fiel.setForeground(AMBER);
        fiel.setBackground(AMBER_LIGHT);
        fiel.setOpaque(true);
        fiel.setBorder(new EmptyBorder(2, 8, 2, 8));
        nameRow.add(nome);
        if (c.visitas() >= 3) nameRow.add(fiel);

        info.add(nameRow);
        info.add(Box.createVerticalStrut(4));
        info.add(quickLabel("# " + c.cpf(), TEXT_LIGHT));
        info.add(quickLabel(c.email(), TEXT_LIGHT));

        card.add(av,   BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);

        p.add(card);
        p.add(Box.createVerticalStrut(10));

        // ── Card bônus ──
        RoundedPanel bonus = new RoundedPanel(12);
        bonus.setBackground(AMBER);
        bonus.setBorder(new EmptyBorder(16, 18, 16, 18));
        bonus.setLayout(new BorderLayout());
        bonus.setAlignmentX(Component.LEFT_ALIGNMENT);
        bonus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel bonusLeft = new JPanel();
        bonusLeft.setOpaque(false);
        bonusLeft.setLayout(new BoxLayout(bonusLeft, BoxLayout.Y_AXIS));
        JLabel lbTitle = quickLabel("\u2606 Saldo de B\u00f4nus", new Color(0xFFE5C0));
        lbTitle.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel lbVal = new JLabel("R$ " + fmt(c.bonus()));
        lbVal.setFont(new Font("Arial", Font.BOLD, 26));
        lbVal.setForeground(Color.WHITE);
        JLabel lbVisitas = quickLabel("Acumulado em " + c.visitas() + " visitas", new Color(0xFFE5C0));
        lbVisitas.setFont(new Font("Arial", Font.PLAIN, 11));
        bonusLeft.add(lbTitle);
        bonusLeft.add(lbVal);
        bonusLeft.add(lbVisitas);

        JPanel bonusRight = new JPanel();
        bonusRight.setOpaque(false);
        bonusRight.setLayout(new BoxLayout(bonusRight, BoxLayout.Y_AXIS));
        JLabel lTotalLabel = quickLabel("Total gasto", new Color(0xFFE5C0));
        lTotalLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel lTotal = new JLabel("R$ " + fmt(c.totalGasto()));
        lTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lTotal.setForeground(Color.WHITE);
        bonusRight.add(lTotalLabel);
        bonusRight.add(lTotal);

        bonus.add(bonusLeft,  BorderLayout.CENTER);
        bonus.add(bonusRight, BorderLayout.EAST);

        p.add(bonus);
        p.add(Box.createVerticalStrut(10));

        // ── Histórico ──
        RoundedPanel hist = new RoundedPanel(12);
        hist.setBackground(CARD_BG);
        hist.setBorder(new EmptyBorder(16, 18, 16, 18));
        hist.setLayout(new BoxLayout(hist, BoxLayout.Y_AXIS));
        hist.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel histTitle = sectionTitle("Hist\u00f3rico de pedidos");
        histTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        hist.add(histTitle);
        hist.add(Box.createVerticalStrut(10));

        String[][] rows = {
            {"Mesa 02","12/06/2026 · 6 itens","R$ 196,00"},
            {"Mesa 07","28/05/2026 · 3 itens","R$ 84,50"},
            {"Mesa 02","10/05/2026 · 4 itens","R$ 112,00"}
        };
        for (String[] row : rows) {
            JPanel hr = new JPanel(new BorderLayout(10, 0));
            hr.setOpaque(false);
            hr.setBorder(new EmptyBorder(8, 0, 8, 0));
            hr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            JLabel icon2 = new JLabel("\uD83D\uDCCB");
            icon2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            icon2.setForeground(TEXT_LIGHT);
            JPanel mid = new JPanel();
            mid.setOpaque(false);
            mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
            mid.add(quickLabel(row[0], TEXT_DARK));
            mid.add(quickLabel(row[1], TEXT_LIGHT));
            JLabel val = quickLabel(row[2], TEXT_DARK);
            val.setFont(new Font("Arial", Font.BOLD, 12));
            hr.add(icon2, BorderLayout.WEST);
            hr.add(mid,   BorderLayout.CENTER);
            hr.add(val,   BorderLayout.EAST);
            hist.add(hr);
            hist.add(new JSeparator());
        }
        p.add(hist);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UTILITÁRIOS
    // ═══════════════════════════════════════════════════════════════════════════
    JLabel quickLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(color);
        return l;
    }

    JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(TEXT_DARK);
        return l;
    }

    JLabel colorDot(Color c) {
        JLabel d = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillOval(2, 4, 10, 10);
                g2.dispose();
            }
        };
        d.setPreferredSize(new Dimension(14, 18));
        return d;
    }

    JPanel infoRow(String icon, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel lbl = new JLabel((icon.isEmpty() ? "" : icon + " ") + label + ":");
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(TEXT_LIGHT);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.BOLD, 12));
        val.setForeground(TEXT_DARK);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

    JButton tabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(active ? TEXT_DARK : TEXT_MID);
        btn.setBackground(active ? Color.WHITE : BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(DIVIDER, 1, true),
            new EmptyBorder(6, 20, 6, 20)
        ));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    JButton roundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        return btn;
    }

    JLabel makeAvatar(String initials, int size) {
        return new JLabel(initials) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMBER);
                g2.fillOval(0, 0, size, size);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, size / 3));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t,
                    (size - fm.stringWidth(t)) / 2,
                    (size + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
            @Override public Dimension getMinimumSize()   { return getPreferredSize(); }
        };
    }

    String fmt(double v) {
        return String.format("%,.2f", v).replace(",", "X").replace(".", ",").replace("X", ".");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // INNER CLASS — RoundedPanel
    // ═══════════════════════════════════════════════════════════════════════════
    static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }

        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0xEAE7E2));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, radius, radius));
            g2.dispose();
        }

        @Override public boolean isOpaque() { return false; }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Look & Feel do sistema (para fontes nativas)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Suavização de fontes (importante no Windows)
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(MainFrame::new);
    }
}