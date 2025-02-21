import javax.swing.SwingUtilities;

import gui.VentanaPrincipal;

public class App {
    public static void main(String[] args) throws Exception {
         SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
