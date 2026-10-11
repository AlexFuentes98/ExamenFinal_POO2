
package cl.biblioteca.controller;

import cl.biblioteca.dao.LibroDAO;
import cl.biblioteca.model.Libro;

import java.sql.SQLException;
import java.util.List;

public class LibroController {

    private final LibroDAO libroDAO = new LibroDAO();

    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.listar();
    }

    public boolean agregarLibro(
            String titulo,
            String autor,
            String isbn,
            String editorial,
            int stock,
            int idCategoria) throws SQLException {

        validarDatos(titulo, autor, isbn, editorial,
                stock, idCategoria);

        Libro libro = new Libro(
                0,
                titulo.trim(),
                autor.trim(),
                isbn.trim(),
                editorial.trim(),
                stock,
                idCategoria
        );

        return libroDAO.insertar(libro);
    }

    public boolean actualizarLibro(
            int id,
            String titulo,
            String autor,
            String isbn,
            String editorial,
            int stock,
            int idCategoria) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un libro válido."
            );
        }

        validarDatos(titulo, autor, isbn, editorial,
                stock, idCategoria);

        Libro libro = new Libro(
                id,
                titulo.trim(),
                autor.trim(),
                isbn.trim(),
                editorial.trim(),
                stock,
                idCategoria
        );

        return libroDAO.actualizar(libro);
    }

    public boolean eliminarLibro(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un libro válido."
            );
        }

        return libroDAO.eliminar(id);
    }

    private void validarDatos(
            String titulo,
            String autor,
            String isbn,
            String editorial,
            int stock,
            int idCategoria) {

        if (titulo == null || titulo.isBlank()
                || autor == null || autor.isBlank()
                || isbn == null || isbn.isBlank()
                || editorial == null || editorial.isBlank()) {

            throw new IllegalArgumentException(
                    "Debes completar todos los campos."
            );
        }

        if (stock < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo."
            );
        }

        if (idCategoria <= 0) {
            throw new IllegalArgumentException(
                    "Debes indicar una categoría válida."
            );
        }

        if (titulo.trim().length() > 200
                || autor.trim().length() > 100
                || isbn.trim().length() > 20
                || editorial.trim().length() > 100) {

            throw new IllegalArgumentException(
                    "Uno o más campos superan el largo permitido."
            );
        }
    }
}
