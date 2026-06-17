package app;
import javax.swing.SwingUtilities;
import view.MainFrame;

/**
 * Ponto de entrada principal do projeto MEZA.
 * Responsável por inicializar a interface gráfica integrada com o backend.
 */
public class Main {

    public static void main(String[] args) {
        // Altera o visual para o padrão do sistema operacional moderno se disponível
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Caso falhe, mantém o padrão clássico do Java (Metal/Nimbus)
        }

        // Executa a inicialização do MainFrame na Thread de Despacho de Eventos do Swing (Thread-safe)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("[MEZA] Inicializando interface e carregando dados do pacote Model...");
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}