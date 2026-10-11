
package cl.biblioteca.view;

import cl.biblioteca.controller.LibroController;
import cl.biblioteca.model.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LibrosFrame extends JFrame {

    private final LibroController libroController =
            new LibroController();

    private final DefaultTableModel modelo;
    private final JTable tabla;

    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtAutor = new JTextField();
    private final JTextField txtISBN = new JTextField();
    private final JTextField txtEditorial = new JTextField();
    private final JTextField txtStock = new JTextField();
    private final JTextField txtCategoria = new JTextField();

    private int idSeleccionado = -1;

    public LibrosFrame(boolean esBibliotecario) {

        setTitle("Biblioteca Escolar - Gestión de Libros");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modelo = new DefaultTableModel(
                new String[]{
                        "ID", "Título", "Autor", "ISBN",
                        "Editorial", "Stock", "ID Categoría"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        principal.add(
                new JScrollPane(tabla),
                BorderLayout.CENTER
        );

        JPanel formulario = new JPanel(new GridLayout(6, 2, 8, 8));

        formulario.add(new JLabel("Título:"));
        formulario.add(txtTitulo);

        formulario.add(new JLabel("Autor:"));
        formulario.add(txtAutor);

        formulario.add(new JLabel("ISBN:"));
        formulario.add(txtISBN);

        formulario.add(new JLabel("Editorial:"));
        formulario.add(txtEditorial);

        formulario.add(new JLabel("Stock:"));
        formulario.add(txtStock);

        formulario.add(new JLabel("ID Categoría:"));
        formulario.add(txtCategoria);

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


        btnAgregar.setEnabled(esBibliotecario);
        btnActualizar.setEnabled(esBibliotecario);
        btnEliminar.setEnabled(esBibliotecario);

        txtTitulo.setEditable(esBibliotecario);
        txtAutor.setEditable(esBibliotecario);
        txtISBN.setEditable(esBibliotecario);
        txtEditorial.setEditable(esBibliotecario);
        txtStock.setEditable(esBibliotecario);
        txtCategoria.setEditable(esBibliotecario);


        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarLibro();
            }
        });


        btnAgregar.addActionListener(e -> guardarLibro(false));
        btnActualizar.addActionListener(e -> guardarLibro(true));
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRecargar.addActionListener(e -> cargarLibros());

        cargarLibros();
    }

    private void cargarLibros() {

        try {
            List<Libro> libros = libroController.listarLibros();

            modelo.setRowCount(0);

            for (Libro libro : libros) {
                modelo.addRow(new Object[]{
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getIsbn(),
                        libro.getEditorial(),
                        libro.getStock(),
                        libro.getIdCategoria()
                });
            }

        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void seleccionarLibro() {

        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        idSeleccionado = (int) modelo.getValueAt(filaModelo, 0);

        txtTitulo.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 1))
        );

        txtAutor.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 2))
        );

        txtISBN.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 3))
        );

        txtEditorial.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 4))
        );

        txtStock.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 5))
        );

        txtCategoria.setText(
                String.valueOf(modelo.getValueAt(filaModelo, 6))
        );
    }

    private void guardarLibro(boolean actualizar) {

        try {
            String titulo = txtTitulo.getText().trim();
            String autor = txtAutor.getText().trim();
            String isbn = txtISBN.getText().trim();
            String editorial = txtEditorial.getText().trim();

            if (actualizar && idSeleccionado == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecciona un libro para actualizar."
                );
                return;
            }

            int stock;
            int categoria;

            try {
                stock = Integer.parseInt(
                        txtStock.getText().trim()
                );

                categoria = Integer.parseInt(
                        txtCategoria.getText().trim()
                );

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Stock e ID Categoría deben ser números enteros."
                );
                return;
            }

            boolean resultado;

            if (actualizar) {
                resultado = libroController.actualizarLibro(
                        idSeleccionado,
                        titulo,
                        autor,
                        isbn,
                        editorial,
                        stock,
                        categoria
                );
            } else {
                resultado = libroController.agregarLibro(
                        titulo,
                        autor,
                        isbn,
                        editorial,
                        stock,
                        categoria
                );
            }

            if (resultado) {

                JOptionPane.showMessageDialog(
                        this,
                        actualizar
                                ? "Libro actualizado correctamente."
                                : "Libro agregado correctamente."
                );

                limpiarFormulario();
                cargarLibros();

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

    private void eliminarLibro() {

        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un libro para eliminar."
            );
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar este libro?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado =
                    libroController.eliminarLibro(idSeleccionado);

            if (eliminado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Libro eliminado correctamente."
                );

                limpiarFormulario();
                cargarLibros();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el libro para eliminar."
                );
            }

        } catch (SQLException | IllegalArgumentException ex) {
            mostrarError(ex);
        }
    }

    private void limpiarFormulario() {

        idSeleccionado = -1;
        tabla.clearSelection();

        txtTitulo.setText("");
        txtAutor.setText("");
        txtISBN.setText("");
        txtEditorial.setText("");
        txtStock.setText("");
        txtCategoria.setText("");
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
