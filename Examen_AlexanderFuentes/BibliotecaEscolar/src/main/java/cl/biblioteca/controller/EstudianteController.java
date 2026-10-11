
package cl.biblioteca.controller;

import cl.biblioteca.dao.EstudianteDAO;
import cl.biblioteca.model.Estudiante;

import java.sql.SQLException;
import java.util.List;

public class EstudianteController {

    private final EstudianteDAO estudianteDAO;

    public EstudianteController() {
        this.estudianteDAO = new EstudianteDAO();
    }

    public List<Estudiante> listarEstudiantes()
            throws SQLException {

        return estudianteDAO.listar();
    }

    public boolean agregarEstudiante(
            String nombre,
            String rut,
            String curso,
            String correo) throws SQLException {

        validarDatos(nombre, rut, curso, correo);

        Estudiante estudiante = new Estudiante(
                0,
                nombre.trim(),
                rut.trim(),
                curso.trim(),
                correo.trim()
        );

        return estudianteDAO.insertar(estudiante);
    }

    public boolean actualizarEstudiante(
            int id,
            String nombre,
            String rut,
            String curso,
            String correo) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un estudiante válido."
            );
        }

        validarDatos(nombre, rut, curso, correo);

        Estudiante estudiante = new Estudiante(
                id,
                nombre.trim(),
                rut.trim(),
                curso.trim(),
                correo.trim()
        );

        return estudianteDAO.actualizar(estudiante);
    }

    public boolean eliminarEstudiante(int id)
            throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un estudiante válido."
            );
        }

        return estudianteDAO.eliminar(id);
    }

    private void validarDatos(
            String nombre,
            String rut,
            String curso,
            String correo) {

        if (nombre == null || nombre.isBlank()
                || rut == null || rut.isBlank()
                || curso == null || curso.isBlank()
                || correo == null || correo.isBlank()) {

            throw new IllegalArgumentException(
                    "Debes completar todos los campos."
            );
        }

        if (!correo.contains("@")) {
            throw new IllegalArgumentException(
                    "Debes ingresar un correo electrónico válido."
            );
        }

        if (nombre.trim().length() > 100
                || rut.trim().length() > 12
                || curso.trim().length() > 20
                || correo.trim().length() > 100) {

            throw new IllegalArgumentException(
                    "Uno o más campos superan el largo permitido."
            );
        }
    }
}
