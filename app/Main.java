package app;

import javax.swing.SwingUtilities;
import controller.RestauranteController;
import view.MainFrame;

/**
 * Ponto de entrada principal do projeto MEZA.
 * Responsável apenas por inicializar a aplicação.
 */
public class Main {

    public static void main(String[] args) {
        // Altera o visual para o padrão do sistema operacional moderno
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Caso falhe, mantém o padrão clássico do Java
        }

        // Executa a inicialização na Thread de Despacho de Eventos (Thread-safe)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("[MEZA] Inicializando sistema...");
                
                // 1. Cria o controller (gerencia toda a lógica)
                RestauranteController controller = new RestauranteController();
                System.out.println("[MEZA] Controller inicializado");
                
                // 2. Cria e exibe a interface (passa o controller)
                MainFrame frame = new MainFrame(controller);
                frame.setVisible(true);
                
                System.out.println("[MEZA] Interface carregada com sucesso!");
            }
        });
    }
}