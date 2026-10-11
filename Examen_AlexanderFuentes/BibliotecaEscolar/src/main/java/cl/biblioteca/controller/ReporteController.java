
package cl.biblioteca.controller;

import cl.biblioteca.dao.ReporteDAO;

import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class ReporteController {

    private final ReporteDAO reporteDAO;

    public ReporteController() {
        this.reporteDAO = new ReporteDAO();
    }

    public DefaultTableModel librosMasPrestados()
            throws SQLException {

        return reporteDAO.librosMasPrestados();
    }

    public DefaultTableModel prestamosActivos()
            throws SQLException {

        return reporteDAO.prestamosActivos();
    }

    public DefaultTableModel historialPorEstudiante(int idEstudiante)
            throws SQLException {

        if (idEstudiante <= 0) {
            throw new IllegalArgumentException(
                    "Debes ingresar un ID de estudiante válido."
            );
        }

        return reporteDAO.historialPorEstudiante(idEstudiante);
    }
}
