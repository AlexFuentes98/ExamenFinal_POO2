
package cl.biblioteca.controller;

import cl.biblioteca.dao.CategoriaDAO;
import cl.biblioteca.model.Categoria;

import java.sql.SQLException;
import java.util.List;

public class CategoriaController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.listar();
    }

    public boolean agregarCategoria(String nombre) throws SQLException {

        validarNombre(nombre);

        Categoria categoria = new Categoria(
                0,
                nombre.trim()
        );

        return categoriaDAO.insertar(categoria);
    }

    public boolean actualizarCategoria(
            int id,
            String nombre) throws SQLException {

        validarId(id);
        validarNombre(nombre);

        Categoria categoria = new Categoria(
                id,
                nombre.trim()
        );

        return categoriaDAO.actualizar(categoria);
    }

    public boolean eliminarCategoria(int id) throws SQLException {

        validarId(id);

        return categoriaDAO.eliminar(id);
    }

    private void validarId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar una categoría válida."
            );
        }
    }

    private void validarNombre(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la categoría es obligatorio."
            );
        }

        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre de la categoría no puede superar los 100 caracteres."
            );
        }
    }
}
