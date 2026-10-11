
package cl.biblioteca.view;

import cl.biblioteca.dao.UsuarioDAO;
import cl.biblioteca.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame {

    private final JTextField txtCorreo;
    private final JPasswordField txtContrasena;
    private final JButton btnIngresar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private boolean autenticacionEnCurso = false;

    public LoginFrame() {

        setTitle("Biblioteca Escolar - Inicio de Sesión");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("BIBLIOTECA ESCOLAR");

        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;

        panel.add(new JLabel("Correo:"), gbc);

        txtCorreo = new JTextField(20);

        gbc.gridx = 1;
        panel.add(txtCorreo, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;

        panel.add(new JLabel("Contraseña:"), gbc);

        txtContrasena = new JPasswordField(20);

        gbc.gridx = 1;
        panel.add(txtContrasena, gbc);

        btnIngresar = new JButton("Iniciar sesión");

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        panel.add(btnIngresar, gbc);

        btnIngresar.addActionListener(e -> iniciarSesion());

        getRootPane().setDefaultButton(btnIngresar);

        add(panel);
    }

    private void iniciarSesion() {

        if (autenticacionEnCurso) {
            return;
        }

        String correo = txtCorreo.getText().trim();

        char[] caracteres = txtContrasena.getPassword();
        String contrasena = new String(caracteres);

        Arrays.fill(caracteres, '\0');

        if (correo.isEmpty() || contrasena.isEmpty()) {

            mostrarMensaje(
                    "Debes ingresar correo y contraseña.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        cambiarEstadoAutenticacion(true);

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {

            @Override
            protected Usuario doInBackground() throws SQLException {

                return usuarioDAO.autenticar(correo, contrasena);
            }

            @Override
            protected void done() {

                try {

                    Usuario usuario = get();

                    if (usuario == null) {

                        mostrarMensaje(
                                "Correo o contraseña incorrectos.",
                                "Acceso denegado",
                                JOptionPane.ERROR_MESSAGE
                        );

                        txtContrasena.setText("");

                        return;
                    }

                    String rol = usuario.getRol();

                    if (rol == null ||
                            (!rol.equalsIgnoreCase("bibliotecario") &&
                                    !rol.equalsIgnoreCase("estudiante"))) {

                        mostrarMensaje(
                                "El usuario no tiene un rol válido.",
                                "Acceso denegado",
                                JOptionPane.ERROR_MESSAGE
                        );

                        txtContrasena.setText("");

                        return;
                    }

                    mostrarMensaje(
                            "¡Bienvenido, " + usuario.getNombre() + "!\n"
                                    + "Rol: " + rol,
                            "Inicio de sesión correcto",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    MenuPrincipalFrame menu =
                            new MenuPrincipalFrame(usuario);

                    menu.setVisible(true);

                    LoginFrame.this.dispose();

                } catch (InterruptedException ex) {

                    Thread.currentThread().interrupt();

                    mostrarMensaje(
                            "El inicio de sesión fue interrumpido.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                } catch (ExecutionException ex) {

                    Throwable causa = ex.getCause();

                    mostrarMensaje(
                            "No fue posible iniciar sesión:\n"
                                    + (causa != null
                                    ? causa.getMessage()
                                    : ex.getMessage()),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                } finally {

                    cambiarEstadoAutenticacion(false);
                }
            }
        };

        worker.execute();
    }

    private void cambiarEstadoAutenticacion(boolean enCurso) {

        autenticacionEnCurso = enCurso;

        btnIngresar.setEnabled(!enCurso);
        txtCorreo.setEnabled(!enCurso);
        txtContrasena.setEnabled(!enCurso);
    }

    private void mostrarMensaje(
            String mensaje,
            String titulo,
            int tipo) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                titulo,
                tipo
        );
    }
}
