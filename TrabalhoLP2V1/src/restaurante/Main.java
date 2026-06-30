package restaurante;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Melhora a qualidade gráfica geral: suaviza fontes e ativa renderização de alta qualidade
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("sun.java2d.opengl", "true");

        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantém padrão
        }

        // Tooltips mais legíveis em qualquer tema
        javax.swing.UIManager.put("ToolTip.font", new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

        SwingUtilities.invokeLater(() -> {
            RestauranteController controller = new RestauranteController();
            LoginScreen loginScreen = new LoginScreen(controller);
            loginScreen.setVisible(true);
        });
    }
}
