package restaurante;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Gerenciador global de tema (claro/escuro).
 * Aplica recoloração recursiva a qualquer hierarquia de componentes Swing,
 * mapeando as cores claras conhecidas do sistema para equivalentes escuros,
 * e restaurando as cores originais ao voltar para o modo claro.
 */
public class ThemeManager {

    public static boolean dark = false;

    private static final Map<Integer, Color> BG_MAP = new HashMap<>();
    private static final Map<Integer, Color> FG_MAP = new HashMap<>();

    private static final Map<Component, Color> ORIG_BG = new IdentityHashMap<>();
    private static final Map<Component, Color> ORIG_FG = new IdentityHashMap<>();

    private static void bg(int r, int g, int b, int dr, int dg, int db) {
        BG_MAP.put(new Color(r, g, b).getRGB(), new Color(dr, dg, db));
    }

    private static void fg(int r, int g, int b, int dr, int dg, int db) {
        FG_MAP.put(new Color(r, g, b).getRGB(), new Color(dr, dg, db));
    }

    static {
        // ── Fundos ───────────────────────────────────────────────
        bg(245, 246, 250, 22, 23, 28);
        bg(245, 245, 245, 24, 25, 30);
        bg(255, 255, 255, 34, 35, 42);
        bg(248, 248, 248, 30, 31, 38);
        bg(248, 249, 253, 30, 31, 38);
        bg(248, 255, 248, 26, 34, 28);
        bg(235, 247, 235, 26, 38, 28);
        bg(220, 240, 220, 32, 46, 34);
        bg(200, 230, 200, 44, 60, 46);
        bg(180, 220, 180, 56, 76, 58);
        bg(255, 235, 230, 44, 30, 28);
        bg(232, 235, 246, 40, 42, 54);
        bg(228, 232, 244, 38, 40, 52);
        bg(218, 222, 238, 50, 52, 68);
        bg(220, 224, 238, 50, 52, 68);
        bg(210, 215, 230, 56, 58, 74);
        bg(225, 228, 240, 48, 50, 64);
        bg(230, 230, 230, 44, 44, 48);
        bg(230, 232, 242, 44, 46, 58);
        bg(228, 230, 242, 44, 46, 58);
        bg(200, 200, 200, 66, 66, 72);
        bg(240, 240, 240, 40, 41, 48);
        bg(232, 232, 232, 42, 42, 46);

        // ── Textos ───────────────────────────────────────────────
        fg(51, 51, 51, 230, 232, 238);
        fg(22, 24, 35, 236, 238, 244);
        fg(40, 44, 60, 222, 225, 235);
        fg(60, 65, 80, 205, 208, 220);
        fg(68, 68, 68, 190, 192, 200);
        fg(110, 115, 140, 160, 165, 185);
        fg(110, 118, 145, 160, 165, 185);
        fg(110, 118, 148, 160, 165, 185);
        fg(100, 105, 150, 165, 170, 195);
        fg(100, 110, 165, 165, 172, 210);
        fg(100, 110, 200, 170, 178, 225);
        fg(120, 120, 120, 155, 155, 162);
        fg(120, 128, 160, 155, 160, 185);
        fg(140, 145, 170, 150, 155, 178);
        fg(70, 78, 110, 175, 182, 210);
        fg(60, 65, 90, 195, 198, 212);
        fg(80, 85, 110, 175, 180, 200);
        fg(80, 85, 115, 175, 180, 200);
        fg(60, 65, 100, 190, 195, 212);
        fg(80, 80, 80, 180, 180, 188);
        fg(100, 100, 100, 175, 175, 182);
        fg(150, 80, 40, 220, 150, 100);
        fg(60, 80, 60, 150, 200, 150);
        fg(40, 80, 40, 140, 200, 140);
        fg(60, 130, 60, 130, 195, 130);
        fg(60, 100, 60, 140, 195, 140);
        fg(60, 150, 70, 130, 210, 140);
    }

    /** Alterna entre modo claro e escuro globalmente. */
    public static void toggle() {
        dark = !dark;
        updateUIManagerDefaults();
    }

    /** Aplica o tema atual (recursivamente) a um componente e seus filhos. */
    public static void apply(Component c) {
        if (c == null) return;
        recolor(c);
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                apply(child);
            }
        }
        c.invalidate();
        c.repaint();
    }

    private static void recolor(Component c) {
        Color bgColor = c.getBackground();
        Color fgColor = c.getForeground();

        if (dark) {
            if (bgColor != null) {
                Color mapped = BG_MAP.get(bgColor.getRGB());
                if (mapped != null) {
                    if (!ORIG_BG.containsKey(c)) ORIG_BG.put(c, bgColor);
                    c.setBackground(mapped);
                }
            }
            if (fgColor != null) {
                Color mapped = FG_MAP.get(fgColor.getRGB());
                if (mapped != null) {
                    if (!ORIG_FG.containsKey(c)) ORIG_FG.put(c, fgColor);
                    c.setForeground(mapped);
                }
            }
        } else {
            Color ob = ORIG_BG.get(c);
            if (ob != null) c.setBackground(ob);
            Color of = ORIG_FG.get(c);
            if (of != null) c.setForeground(of);
        }

        if (c instanceof JTable) {
            JTable t = (JTable) c;
            JTableHeader header = t.getTableHeader();
            if (dark) {
                t.setGridColor(new Color(58, 60, 76));
                if (header != null) {
                    header.setBackground(new Color(40, 42, 54));
                    header.setForeground(new Color(220, 222, 232));
                }
            } else {
                t.setGridColor(new Color(228, 232, 244));
                if (header != null) {
                    // Restaurado pela recolor() padrão acima via ORIG_BG/ORIG_FG
                }
            }
        }
    }

    private static void updateUIManagerDefaults() {
        if (dark) {
            UIManager.put("OptionPane.background", new Color(34, 35, 42));
            UIManager.put("Panel.background", new Color(34, 35, 42));
            UIManager.put("OptionPane.messageForeground", new Color(230, 232, 238));
            UIManager.put("Button.background", new Color(50, 52, 66));
            UIManager.put("Button.foreground", new Color(230, 232, 238));
            UIManager.put("TextField.background", new Color(44, 46, 58));
            UIManager.put("TextField.foreground", new Color(230, 232, 238));
            UIManager.put("Label.foreground", new Color(230, 232, 238));
        } else {
            UIManager.put("OptionPane.background", null);
            UIManager.put("Panel.background", null);
            UIManager.put("OptionPane.messageForeground", null);
            UIManager.put("Button.background", null);
            UIManager.put("Button.foreground", null);
            UIManager.put("TextField.background", null);
            UIManager.put("TextField.foreground", null);
            UIManager.put("Label.foreground", null);
        }
    }

    /** Cria um botão padrão de alternância de tema, pronto para ser adicionado a um cabeçalho escuro. */
    public static JButton createToggleButton(Component root) {
        JButton btn = new JButton(dark ? "☀ Modo Claro" : "🌙 Modo Escuro");
        btn.setBackground(new Color(50, 52, 70));
        btn.setForeground(new Color(200, 205, 220));
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            toggle();
            apply(root);
            if (root instanceof Container) ((Container) root).revalidate();
            root.repaint();
            btn.setText(dark ? "☀ Modo Claro" : "🌙 Modo Escuro");
        });
        return btn;
    }
}
