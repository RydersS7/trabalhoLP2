package restaurante;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantém padrão
        }

        SwingUtilities.invokeLater(() -> {
            RestauranteController controller = new RestauranteController();
            LoginScreen loginScreen = new LoginScreen(controller);
            loginScreen.setVisible(true);
        });
    }
}
