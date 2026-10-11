
package cl.biblioteca.dao;

import cl.biblioteca.config.DatabaseConnection;
import cl.biblioteca.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public List<Libro> listar() throws SQLException {
        List<Libro> libros = new ArrayList<>();

        String sql = """
                SELECT id, titulo, autor, isbn, editorial,
                       stock, id_categoria
                FROM libros
                ORDER BY id
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(convertirLibro(rs));
            }
        }

        return libros;
    }

    public boolean insertar(Libro libro) throws SQLException {
        String sql = """
                INSERT INTO libros
                (titulo, autor, isbn, editorial, stock, id_categoria)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            cargarParametros(ps, libro);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Libro libro) throws SQLException {
        String sql = """
                UPDATE libros
                SET titulo = ?, autor = ?, isbn = ?,
                    editorial = ?, stock = ?, id_categoria = ?
                WHERE id = ?
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            cargarParametros(ps, libro);
            ps.setInt(7, libro.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Libro buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT id, titulo, autor, isbn, editorial,
                       stock, id_categoria
                FROM libros
                WHERE id = ?
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertirLibro(rs);
                }
            }
        }

        return null;
    }

    private void cargarParametros(
            PreparedStatement ps, Libro libro) throws SQLException {

        ps.setString(1, libro.getTitulo());
        ps.setString(2, libro.getAutor());
        ps.setString(3, libro.getIsbn());
        ps.setString(4, libro.getEditorial());
        ps.setInt(5, libro.getStock());
        ps.setInt(6, libro.getIdCategoria());
    }

    private Libro convertirLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                rs.getInt("id_categoria")
        );
    }
}
