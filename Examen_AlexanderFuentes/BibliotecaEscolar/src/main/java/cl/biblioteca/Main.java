

package cl.biblioteca;

import cl.biblioteca.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println(
                        "No se pudo aplicar el estilo del sistema."
                );
            }

            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
