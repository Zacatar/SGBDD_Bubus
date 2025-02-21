import javax.swing.SwingUtilities;

import gui.Gui;

public class App {
    public static void main(String[] args) throws Exception {
         SwingUtilities.invokeLater(() -> {
            Gui ventana = new Gui();
            ventana.setVisible(true);
        });
    }
}
