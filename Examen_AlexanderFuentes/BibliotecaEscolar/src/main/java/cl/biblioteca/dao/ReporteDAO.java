
package cl.biblioteca.dao;

import cl.biblioteca.config.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReporteDAO {

    public DefaultTableModel librosMasPrestados() throws SQLException {

        String sql = """
                SELECT l.id AS ID,
                       l.titulo AS Libro,
                       l.autor AS Autor,
                       COUNT(p.id) AS Total_Prestamos
                FROM libros l
                LEFT JOIN prestamos p ON p.id_libro = l.id
                GROUP BY l.id, l.titulo, l.autor
                ORDER BY Total_Prestamos DESC, l.titulo
                """;

        return ejecutarConsulta(sql);
    }

    public DefaultTableModel prestamosActivos() throws SQLException {

        String sql = """
                SELECT p.id AS ID_Prestamo,
                       e.nombre AS Estudiante,
                       l.titulo AS Libro,
                       p.fecha_prestamo AS Fecha_Prestamo,
                       p.fecha_devolucion AS Vencimiento,
                       CASE
                           WHEN CURRENT_DATE() > p.fecha_devolucion
                               THEN 'Atrasado'
                           ELSE 'Vigente'
                       END AS Estado
                FROM prestamos p
                JOIN estudiantes e ON e.id = p.id_estudiante
                JOIN libros l ON l.id = p.id_libro
                WHERE p.devuelto = FALSE
                ORDER BY p.fecha_devolucion, p.id
                """;

        return ejecutarConsulta(sql);
    }

    public DefaultTableModel historialPorEstudiante(int idEstudiante)
            throws SQLException {

        if (idEstudiante <= 0) {
            throw new IllegalArgumentException(
                    "Debes ingresar un ID de estudiante válido."
            );
        }

        String sql = """
                SELECT p.id AS ID_Prestamo,
                       e.nombre AS Estudiante,
                       l.titulo AS Libro,
                       p.fecha_prestamo AS Fecha_Prestamo,
                       p.fecha_devolucion AS Vencimiento,
                       p.fecha_devolucion_real AS Devolucion_Real,
                       CASE
                           WHEN p.devuelto = TRUE
                                AND p.fecha_devolucion_real IS NULL
                               THEN 'Devuelto (fecha no registrada)'
                           WHEN p.devuelto = TRUE
                                AND p.fecha_devolucion_real > p.fecha_devolucion
                               THEN 'Devuelto con atraso'
                           WHEN p.devuelto = TRUE
                               THEN 'Devuelto a tiempo'
                           WHEN CURRENT_DATE() > p.fecha_devolucion
                               THEN 'Atrasado'
                           ELSE 'Activo'
                       END AS Estado
                FROM prestamos p
                JOIN estudiantes e ON e.id = p.id_estudiante
                JOIN libros l ON l.id = p.id_libro
                WHERE p.id_estudiante = ?
                ORDER BY p.fecha_prestamo DESC, p.id DESC
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                return construirModelo(rs);
            }
        }
    }

    private DefaultTableModel ejecutarConsulta(String sql)
            throws SQLException {

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return construirModelo(rs);
        }
    }

    private DefaultTableModel construirModelo(ResultSet rs)
            throws SQLException {

        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();

        String[] encabezados = new String[columnas];

        for (int i = 1; i <= columnas; i++) {
            encabezados[i - 1] = meta.getColumnLabel(i);
        }

        DefaultTableModel modelo = new DefaultTableModel(
                encabezados, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        while (rs.next()) {

            Object[] fila = new Object[columnas];

            for (int i = 1; i <= columnas; i++) {
                fila[i - 1] = rs.getObject(i);
            }

            modelo.addRow(fila);
        }

        return modelo;
    }
}
