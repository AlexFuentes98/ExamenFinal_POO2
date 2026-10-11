
package cl.biblioteca.dao;

import cl.biblioteca.config.DatabaseConnection;
import cl.biblioteca.model.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public synchronized boolean registrarPrestamo(
            int idEstudiante, int idLibro) throws SQLException {

        String descontarStock = """
                UPDATE libros
                SET stock = stock - 1
                WHERE id = ? AND stock > 0
                """;

        String insertarPrestamo = """
                INSERT INTO prestamos
                (id_estudiante, id_libro, fecha_prestamo,
                 fecha_devolucion, devuelto)
                VALUES (?, ?, ?, ?, FALSE)
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection()) {

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                             conn.prepareStatement(descontarStock)) {

                    ps.setInt(1, idLibro);

                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                             conn.prepareStatement(insertarPrestamo)) {

                    LocalDate hoy = LocalDate.now();
                    LocalDate vencimiento = hoy.plusDays(7);

                    ps.setInt(1, idEstudiante);
                    ps.setInt(2, idLibro);
                    ps.setDate(3, Date.valueOf(hoy));
                    ps.setDate(4, Date.valueOf(vencimiento));

                    ps.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public synchronized boolean registrarDevolucion(int idPrestamo)
            throws SQLException {

        String buscarPrestamo = """
                SELECT id_libro, devuelto
                FROM prestamos
                WHERE id = ?
                FOR UPDATE
                """;

        String actualizarPrestamo = """
        UPDATE prestamos
        SET devuelto = TRUE,
            fecha_devolucion_real = CURRENT_DATE()
        WHERE id = ? AND devuelto = FALSE
        """;

        String devolverStock = """
                UPDATE libros
                SET stock = stock + 1
                WHERE id = ?
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection()) {

            conn.setAutoCommit(false);

            try {
                int idLibro;

                try (PreparedStatement ps =
                             conn.prepareStatement(buscarPrestamo)) {

                    ps.setInt(1, idPrestamo);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next() || rs.getBoolean("devuelto")) {
                            conn.rollback();
                            return false;
                        }

                        idLibro = rs.getInt("id_libro");
                    }
                }

                try (PreparedStatement ps =
                             conn.prepareStatement(actualizarPrestamo)) {

                    ps.setInt(1, idPrestamo);

                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                             conn.prepareStatement(devolverStock)) {

                    ps.setInt(1, idLibro);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "No se encontró el libro del préstamo."
                        );
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public List<Prestamo> listar() throws SQLException {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
                SELECT id, id_estudiante, id_libro,
                       fecha_prestamo, fecha_devolucion, devuelto
                FROM prestamos
                ORDER BY id DESC
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prestamos.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion") != null
                                ? rs.getDate("fecha_devolucion").toLocalDate()
                                : null,
                        rs.getBoolean("devuelto")
                ));
            }
        }

        return prestamos;
    }
}
