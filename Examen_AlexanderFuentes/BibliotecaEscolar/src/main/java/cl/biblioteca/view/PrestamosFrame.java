
package cl.biblioteca.view;

import cl.biblioteca.controller.PrestamoController;
import cl.biblioteca.model.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PrestamosFrame extends JFrame {

    // Controlador MVC: la vista no accede directamente al DAO.
    private final PrestamoController prestamoController =
            new PrestamoController();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{
                    "ID", "ID Estudiante", "ID Libro",
                    "Fecha Préstamo", "Vencimiento",
                    "Estado", "Atrasado"
            }, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modelo);

    private final JTextField txtEstudiante = new JTextField(8);
    private final JTextField txtLibro = new JTextField(8);

    private final JButton btnPrestar =
            new JButton("Registrar préstamo");

    private final JButton btnDevolver =
            new JButton("Registrar devolución");

    private final JButton btnRecargar =
            new JButton("Recargar");

    private boolean operacionEnCurso = false;

    public PrestamosFrame() {

        setTitle("Biblioteca Escolar - Préstamos y Devoluciones");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        principal.add(
                new JScrollPane(tabla),
                BorderLayout.CENTER
        );

        JPanel formulario = new JPanel(new FlowLayout());

        formulario.add(new JLabel("ID Estudiante:"));
        formulario.add(txtEstudiante);

        formulario.add(new JLabel("ID Libro:"));
        formulario.add(txtLibro);

        formulario.add(btnPrestar);
        formulario.add(btnDevolver);
        formulario.add(btnRecargar);

        principal.add(formulario, BorderLayout.SOUTH);

        add(principal);

        btnPrestar.addActionListener(e -> registrarPrestamo());
        btnDevolver.addActionListener(e -> registrarDevolucion());
        btnRecargar.addActionListener(e -> cargarPrestamos());

        cargarPrestamos();
    }

    // Registra un préstamo utilizando el controlador.
    private void registrarPrestamo() {

        final int idEstudiante;
        final int idLibro;

        try {
            idEstudiante = Integer.parseInt(
                    txtEstudiante.getText().trim()
            );

            idLibro = Integer.parseInt(
                    txtLibro.getText().trim()
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "ID Estudiante e ID Libro deben ser números enteros."
            );
            return;
        }

        ejecutarOperacion(
                () -> prestamoController.registrarPrestamo(
                        idEstudiante,
                        idLibro
                ),
                "Préstamo registrado correctamente.",
                "No hay stock disponible o el libro no existe."
        );
    }

    // Registra una devolución utilizando el controlador.
    private void registrarDevolucion() {

        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un préstamo para devolver."
            );
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        int idPrestamo =
                (int) modelo.getValueAt(filaModelo, 0);

        String estado = String.valueOf(
                modelo.getValueAt(filaModelo, 5)
        );

        if ("Devuelto".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este préstamo ya fue devuelto."
            );
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas registrar la devolución del préstamo "
                        + idPrestamo + "?",
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        ejecutarOperacion(
                () -> prestamoController.registrarDevolucion(
                        idPrestamo
                ),
                "Devolución registrada correctamente.",
                "El préstamo ya fue devuelto o no existe."
        );
    }

    @FunctionalInterface
    private interface OperacionPrestamo {
        boolean ejecutar() throws Exception;
    }

    // Ejecuta préstamos y devoluciones fuera del hilo de Swing.
    private void ejecutarOperacion(
            OperacionPrestamo operacion,
            String mensajeExito,
            String mensajeFallido) {

        if (operacionEnCurso) {
            return;
        }

        cambiarEstadoBotones(true);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                return operacion.ejecutar();
            }

            @Override
            protected void done() {

                cambiarEstadoBotones(false);

                try {
                    boolean resultado = get();

                    JOptionPane.showMessageDialog(
                            PrestamosFrame.this,
                            resultado ? mensajeExito : mensajeFallido
                    );

                    if (resultado) {
                        txtEstudiante.setText("");
                        txtLibro.setText("");
                        cargarPrestamos();
                    }

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();

                    mostrarError(
                            "La operación fue interrumpida."
                    );

                } catch (ExecutionException ex) {

                    Throwable causa = ex.getCause();

                    mostrarError(
                            causa != null
                                    ? causa.getMessage()
                                    : ex.getMessage()
                    );
                }
            }
        };

        worker.execute();
    }

    // Consulta los préstamos mediante el controlador.
    // La consulta se ejecuta en segundo plano.
    private void cargarPrestamos() {

        if (operacionEnCurso) {
            return;
        }

        cambiarEstadoBotones(true);

        SwingWorker<List<Prestamo>, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected List<Prestamo> doInBackground()
                            throws Exception {

                        return prestamoController.listarPrestamos();
                    }

                    @Override
                    protected void done() {

                        try {
                            List<Prestamo> prestamos = get();

                            modelo.setRowCount(0);

                            for (Prestamo prestamo : prestamos) {

                                modelo.addRow(new Object[]{
                                        prestamo.getId(),
                                        prestamo.getIdEstudiante(),
                                        prestamo.getIdLibro(),
                                        prestamo.getFechaPrestamo(),
                                        prestamo.getFechaDevolucion(),
                                        prestamo.isDevuelto()
                                                ? "Devuelto" : "Activo",
                                        prestamo.estaAtrasado()
                                                ? "Sí" : "No"
                                });
                            }

                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();

                            mostrarError(
                                    "La carga de préstamos fue interrumpida."
                            );

                        } catch (ExecutionException ex) {

                            Throwable causa = ex.getCause();

                            mostrarError(
                                    causa != null
                                            ? causa.getMessage()
                                            : ex.getMessage()
                            );

                        } finally {
                            cambiarEstadoBotones(false);
                        }
                    }
                };

        worker.execute();
    }

    // Evita iniciar operaciones simultáneas desde esta ventana.
    private void cambiarEstadoBotones(boolean enCurso) {

        operacionEnCurso = enCurso;

        btnPrestar.setEnabled(!enCurso);
        btnDevolver.setEnabled(!enCurso);
        btnRecargar.setEnabled(!enCurso);

        txtEstudiante.setEnabled(!enCurso);
        txtLibro.setEnabled(!enCurso);
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                "Error: " + mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
