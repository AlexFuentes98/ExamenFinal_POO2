
package cl.biblioteca.controller;

import cl.biblioteca.dao.PrestamoDAO;
import cl.biblioteca.model.Prestamo;

import java.sql.SQLException;
import java.util.List;

public class PrestamoController {

    private final PrestamoDAO prestamoDAO;

    public PrestamoController() {
        this.prestamoDAO = new PrestamoDAO();
    }

    // Obtiene todos los préstamos registrados.
    public List<Prestamo> listarPrestamos() throws SQLException {
        return prestamoDAO.listar();
    }

    // Registra un nuevo préstamo.
    public boolean registrarPrestamo(
            int idEstudiante,
            int idLibro) throws SQLException {

        if (idEstudiante <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un estudiante válido."
            );
        }

        if (idLibro <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un libro válido."
            );
        }

        return prestamoDAO.registrarPrestamo(
                idEstudiante,
                idLibro
        );
    }

    // Registra la devolución de un préstamo existente.
    public boolean registrarDevolucion(int idPrestamo)
            throws SQLException {

        if (idPrestamo <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un préstamo válido."
            );
        }

        return prestamoDAO.registrarDevolucion(idPrestamo);
    }
}
