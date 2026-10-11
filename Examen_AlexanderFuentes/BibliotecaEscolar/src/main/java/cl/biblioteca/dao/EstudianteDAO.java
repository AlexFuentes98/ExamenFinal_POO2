
package cl.biblioteca.dao;

import cl.biblioteca.config.DatabaseConnection;
import cl.biblioteca.model.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    public List<Estudiante> listar() throws SQLException {
        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = """
                SELECT id, nombre, rut, curso, correo
                FROM estudiantes
                ORDER BY id
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                estudiantes.add(convertirEstudiante(rs));
            }
        }

        return estudiantes;
    }

    public Estudiante buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT id, nombre, rut, curso, correo
                FROM estudiantes
                WHERE id = ?
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertirEstudiante(rs);
                }
            }
        }

        return null;
    }

    public boolean insertar(Estudiante estudiante) throws SQLException {
        String sql = """
                INSERT INTO estudiantes (nombre, rut, curso, correo)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            cargarParametros(ps, estudiante);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Estudiante estudiante) throws SQLException {
        String sql = """
                UPDATE estudiantes
                SET nombre = ?, rut = ?, curso = ?, correo = ?
                WHERE id = ?
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            cargarParametros(ps, estudiante);
            ps.setInt(5, estudiante.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void cargarParametros(
            PreparedStatement ps, Estudiante estudiante)
            throws SQLException {

        ps.setString(1, estudiante.getNombre());
        ps.setString(2, estudiante.getRut());
        ps.setString(3, estudiante.getCurso());
        ps.setString(4, estudiante.getCorreo());
    }

    private Estudiante convertirEstudiante(ResultSet rs)
            throws SQLException {

        return new Estudiante(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("curso"),
                rs.getString("correo")
        );
    }
}
