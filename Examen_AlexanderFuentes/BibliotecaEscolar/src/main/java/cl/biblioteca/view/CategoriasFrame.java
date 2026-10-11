
package cl.biblioteca.view;

import cl.biblioteca.controller.CategoriaController;
import cl.biblioteca.model.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class CategoriasFrame extends JFrame {

    private final CategoriaController categoriaController =
            new CategoriaController();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre"}, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modelo);
    private final JTextField txtNombre = new JTextField(25);

    private int idSeleccionado = -1;

    public CategoriasFrame() {

        setTitle("Biblioteca Escolar - Gestión de Categorías");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));

        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        principal.add(
                new JScrollPane(tabla),
                BorderLayout.CENTER
        );

        JPanel formulario = new JPanel(
                new BorderLayout(10, 10)
        );

        formulario.add(
                new JLabel("Nombre de categoría:"),
                BorderLayout.WEST
        );

        formulario.add(
                txtNombre,
                BorderLayout.CENTER
        );

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

        JPanel inferior = new JPanel(
                new BorderLayout(5, 5)
        );

        inferior.add(
                formulario,
                BorderLayout.NORTH
        );

        inferior.add(
                botones,
                BorderLayout.SOUTH
        );

        principal.add(
                inferior,
                BorderLayout.SOUTH
        );

        add(principal);

        tabla.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                seleccionarCategoria();
            }
        });

        btnAgregar.addActionListener(e -> guardar(false));
        btnActualizar.addActionListener(e -> guardar(true));
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRecargar.addActionListener(e -> cargarCategorias());

        cargarCategorias();
    }

    private void cargarCategorias() {

        try {
            modelo.setRowCount(0);

            for (Categoria categoria :
                    categoriaController.listarCategorias()) {

                modelo.addRow(new Object[]{
                        categoria.getId(),
                        categoria.getNombre()
                });
            }

        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void seleccionarCategoria() {

        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        idSeleccionado =
                (int) modelo.getValueAt(filaModelo, 0);

        txtNombre.setText(
                String.valueOf(
                        modelo.getValueAt(filaModelo, 1)
                )
        );
    }

    private void guardar(boolean actualizar) {

        if (actualizar && idSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una categoría para actualizar."
            );

            return;
        }

        try {
            String nombre = txtNombre.getText().trim();

            boolean resultado;

            if (actualizar) {

                resultado =
                        categoriaController.actualizarCategoria(
                                idSeleccionado,
                                nombre
                        );

            } else {

                resultado =
                        categoriaController.agregarCategoria(
                                nombre
                        );
            }

            if (resultado) {

                JOptionPane.showMessageDialog(
                        this,
                        actualizar
                                ? "Categoría actualizada correctamente."
                                : "Categoría agregada correctamente."
                );

                limpiar();
                cargarCategorias();

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

    private void eliminar() {

        if (idSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una categoría para eliminar."
            );

            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar la categoría seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean resultado =
                    categoriaController.eliminarCategoria(
                            idSeleccionado
                    );

            if (resultado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Categoría eliminada correctamente."
                );

                limpiar();
                cargarCategorias();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró la categoría para eliminar."
                );
            }

        } catch (SQLException | IllegalArgumentException ex) {
            mostrarError(ex);
        }
    }

    private void limpiar() {

        idSeleccionado = -1;

        tabla.clearSelection();

        txtNombre.setText("");
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
