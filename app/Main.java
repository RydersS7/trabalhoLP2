package app;

import javax.swing.SwingUtilities;
import controller.RestauranteController;
import view.MainFrame;

public class Main {
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantém padrão
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RestauranteController controller = new RestauranteController();
                MainFrame frame = new MainFrame(controller);
                frame.setVisible(true);
            }
        });
    }
}