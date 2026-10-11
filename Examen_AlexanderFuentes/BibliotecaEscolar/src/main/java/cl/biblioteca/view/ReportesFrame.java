
package cl.biblioteca.view;

import cl.biblioteca.controller.ReporteController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class ReportesFrame extends JFrame {

    private final ReporteController reporteController =
            new ReporteController();

    private final JTable tabla = new JTable();

    private final JTextField txtIdEstudiante =
            new JTextField(8);

    private final JButton btnMasPrestados =
            new JButton("Libros más prestados");

    private final JButton btnActivos =
            new JButton("Préstamos activos");

    private final JButton btnHistorial =
            new JButton("Historial por estudiante");

    private boolean consultaEnCurso = false;

    public ReportesFrame() {

        setTitle("Biblioteca Escolar - Reportes");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(
                new BorderLayout(10, 10)
        );

        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "REPORTES DE BIBLIOTECA",
                SwingConstants.CENTER
        );

        titulo.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        JPanel controles = new JPanel(new FlowLayout());

        controles.add(btnMasPrestados);
        controles.add(btnActivos);
        controles.add(new JLabel("ID Estudiante:"));
        controles.add(txtIdEstudiante);
        controles.add(btnHistorial);

        JPanel superior = new JPanel(new BorderLayout());

        superior.add(
                titulo,
                BorderLayout.NORTH
        );

        superior.add(
                controles,
                BorderLayout.SOUTH
        );

        principal.add(
                superior,
                BorderLayout.NORTH
        );

        principal.add(
                new JScrollPane(tabla),
                BorderLayout.CENTER
        );

        add(principal);

        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        btnMasPrestados.addActionListener(e ->
                cargarReporte(
                        () -> reporteController.librosMasPrestados()
                )
        );

        btnActivos.addActionListener(e ->
                cargarReporte(
                        () -> reporteController.prestamosActivos()
                )
        );

        btnHistorial.addActionListener(e ->
                cargarHistorialEstudiante()
        );

        cargarReporte(
                () -> reporteController.librosMasPrestados()
        );
    }

    private void cargarHistorialEstudiante() {

        final int idEstudiante;

        try {
            idEstudiante = Integer.parseInt(
                    txtIdEstudiante.getText().trim()
            );

            if (idEstudiante <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingresa un ID de estudiante válido.",
                    "Dato inválido",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        cargarReporte(
                () -> reporteController.historialPorEstudiante(
                        idEstudiante
                )
        );
    }

    @FunctionalInterface
    private interface ConsultaReporte {
        DefaultTableModel ejecutar() throws Exception;
    }

    private void cargarReporte(ConsultaReporte consulta) {

        if (consultaEnCurso) {
            return;
        }

        cambiarEstadoControles(true);

        SwingWorker<DefaultTableModel, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected DefaultTableModel doInBackground()
                            throws Exception {

                        return consulta.ejecutar();
                    }

                    @Override
                    protected void done() {

                        try {
                            DefaultTableModel modelo = get();

                            tabla.setModel(modelo);

                        } catch (InterruptedException ex) {

                            Thread.currentThread().interrupt();

                            mostrarError(
                                    "La generación del reporte fue interrumpida."
                            );

                        } catch (ExecutionException ex) {

                            Throwable causa = ex.getCause();

                            mostrarError(
                                    causa != null
                                            ? causa.getMessage()
                                            : ex.getMessage()
                            );

                        } finally {
                            cambiarEstadoControles(false);
                        }
                    }
                };

        worker.execute();
    }

    private void cambiarEstadoControles(boolean enCurso) {

        consultaEnCurso = enCurso;

        btnMasPrestados.setEnabled(!enCurso);
        btnActivos.setEnabled(!enCurso);
        btnHistorial.setEnabled(!enCurso);

        txtIdEstudiante.setEnabled(!enCurso);
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                "Error al generar reporte:\n" + mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
