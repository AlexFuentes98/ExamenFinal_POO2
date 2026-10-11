
package cl.biblioteca.view;

import cl.biblioteca.controller.EstudianteController;
import cl.biblioteca.model.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EstudiantesFrame extends JFrame {

    private final EstudianteController estudianteController =
            new EstudianteController();

    private final DefaultTableModel modelo;
    private final JTable tabla;

    private final JTextField txtNombre = new JTextField();
    private final JTextField txtRut = new JTextField();
    private final JTextField txtCurso = new JTextField();
    private final JTextField txtCorreo = new JTextField();

    private int idSeleccionado = -1;

    public EstudiantesFrame() {
        setTitle("Biblioteca Escolar - Gestión de Estudiantes");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("RUT:"));
        formulario.add(txtRut);

        formulario.add(new JLabel("Curso:"));
        formulario.add(txtCurso);

        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);

        JPanel botones = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnRecargar = new JButton("Recargar");

        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnRecargar);

        JPanel inferior = new JPanel(new BorderLayout(8, 8));
        inferior.add(formulario, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);

        principal.add(inferior, BorderLayout.SOUTH);

        add(principal);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarEstudiante();
            }
        });

        btnAgregar.addActionListener(e -> guardarEstudiante(false));
        btnActualizar.addActionListener(e -> guardarEstudiante(true));
        btnEliminar.addActionListener(e -> eliminarEstudiante());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRecargar.addActionListener(e -> cargarEstudiantes());

        cargarEstudiantes();
    }

    private void cargarEstudiantes() {
        try {
            List<Estudiante> estudiantes =
                    estudianteController.listarEstudiantes();
            modelo.setRowCount(0);

            for (Estudiante estudiante : estudiantes) {
                modelo.addRow(new Object[]{
                        estudiante.getId(),
                        estudiante.getNombre(),
                        estudiante.getRut(),
                        estudiante.getCurso(),
                        estudiante.getCorreo()
                });
            }

        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void seleccionarEstudiante() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        idSeleccionado = (int) modelo.getValueAt(filaModelo, 0);

        txtNombre.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 1))
        );
        txtRut.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 2))
        );
        txtCurso.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 3))
        );
        txtCorreo.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 4))
        );
    }

    private void guardarEstudiante(boolean actualizar) {
        try {
            String nombre = txtNombre.getText().trim();
            String rut = txtRut.getText().trim();
            String curso = txtCurso.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || rut.isEmpty()
                    || curso.isEmpty() || correo.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debes completar todos los campos."
                );
                return;
            }

            if (actualizar && idSeleccionado == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecciona un estudiante para actualizar."
                );
                return;
            }

            boolean resultado = actualizar
                    ? estudianteController.actualizarEstudiante(
                    idSeleccionado, nombre, rut, curso, correo
            )
                    : estudianteController.agregarEstudiante(
                    nombre, rut, curso, correo
            );

            if (resultado) {
                JOptionPane.showMessageDialog(
                        this,
                        actualizar
                                ? "Estudiante actualizado correctamente."
                                : "Estudiante agregado correctamente."
                );

                limpiarFormulario();
                cargarEstudiantes();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se realizaron cambios."
                );
            }

        } catch (SQLException | IllegalArgumentException ex) {
            mostrarError(ex);
        }
    }

    private void eliminarEstudiante() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un estudiante para eliminar."
            );
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar este estudiante?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (estudianteController.eliminarEstudiante(idSeleccionado)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Estudiante eliminado correctamente."
                );

                limpiarFormulario();
                cargarEstudiantes();
            }
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        tabla.clearSelection();

        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
