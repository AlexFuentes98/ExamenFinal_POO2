
package cl.biblioteca.view;

import cl.biblioteca.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {

    private final Usuario usuario;
    private final boolean esBibliotecario;
    private final boolean esEstudiante;

    public MenuPrincipalFrame(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Debe existir un usuario autenticado."
            );
        }

        this.usuario = usuario;

        this.esBibliotecario =
                "bibliotecario".equalsIgnoreCase(usuario.getRol());

        this.esEstudiante =
                "estudiante".equalsIgnoreCase(usuario.getRol());

        setTitle("Biblioteca Escolar - Menú Principal");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(550, 400));

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        JPanel principal = new JPanel(
                new BorderLayout(15, 15)
        );

        principal.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel titulo = new JLabel(
                "SISTEMA DE GESTIÓN DE BIBLIOTECA ESCOLAR",
                SwingConstants.CENTER
        );

        titulo.setFont(
                new Font("Arial", Font.BOLD, 19)
        );

        JLabel bienvenida = new JLabel(
                "Bienvenido: " + usuario.getNombre()
                        + " | Rol: " + usuario.getRol(),
                SwingConstants.CENTER
        );

        JPanel encabezado = new JPanel(
                new GridLayout(2, 1, 5, 5)
        );

        encabezado.add(titulo);
        encabezado.add(bienvenida);

        principal.add(
                encabezado,
                BorderLayout.NORTH
        );

        JPanel botones = new JPanel(
                new GridLayout(3, 2, 15, 15)
        );

        JButton btnLibros = new JButton(
                esBibliotecario
                        ? "Gestión de Libros"
                        : "Consultar Catálogo"
        );

        JButton btnEstudiantes =
                new JButton("Gestión de Estudiantes");

        JButton btnCategorias =
                new JButton("Categorías");

        JButton btnPrestamos =
                new JButton("Préstamos y Devoluciones");

        JButton btnReportes =
                new JButton("Reportes");

        JButton btnSalir =
                new JButton("Cerrar sesión");

        botones.add(btnLibros);
        botones.add(btnEstudiantes);
        botones.add(btnCategorias);
        botones.add(btnPrestamos);
        botones.add(btnReportes);
        botones.add(btnSalir);

        principal.add(
                botones,
                BorderLayout.CENTER
        );

        btnLibros.setEnabled(
                esBibliotecario || esEstudiante
        );

        btnEstudiantes.setEnabled(esBibliotecario);
        btnCategorias.setEnabled(esBibliotecario);
        btnPrestamos.setEnabled(esBibliotecario);
        btnReportes.setEnabled(esBibliotecario);

        btnLibros.addActionListener(e -> {

            if (!esBibliotecario && !esEstudiante) {
                mostrarAccesoDenegado();
                return;
            }

            new LibrosFrame(
                    esBibliotecario
            ).setVisible(true);
        });

        btnEstudiantes.addActionListener(e -> {

            if (!verificarPermisoBibliotecario()) {
                return;
            }

            new EstudiantesFrame().setVisible(true);
        });

        btnCategorias.addActionListener(e -> {

            if (!verificarPermisoBibliotecario()) {
                return;
            }

            new CategoriasFrame().setVisible(true);
        });

        btnPrestamos.addActionListener(e -> {

            if (!verificarPermisoBibliotecario()) {
                return;
            }

            new PrestamosFrame().setVisible(true);
        });

        btnReportes.addActionListener(e -> {

            if (!verificarPermisoBibliotecario()) {
                return;
            }

            new ReportesFrame().setVisible(true);
        });

        btnSalir.addActionListener(
                e -> cerrarSesion()
        );

        add(principal);
    }

    private boolean verificarPermisoBibliotecario() {

        if (!esBibliotecario) {
            mostrarAccesoDenegado();
            return false;
        }

        return true;
    }

    private void mostrarAccesoDenegado() {

        JOptionPane.showMessageDialog(
                this,
                "No tienes permisos para acceder a este módulo.",
                "Acceso denegado",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void cerrarSesion() {

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar sesión?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            new LoginFrame().setVisible(true);

            dispose();
        }
    }
}
