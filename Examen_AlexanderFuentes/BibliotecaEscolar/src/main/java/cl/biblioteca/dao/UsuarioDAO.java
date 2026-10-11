
package cl.biblioteca.dao;

import cl.biblioteca.config.DatabaseConnection;
import cl.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String correo, String contrasena)
            throws SQLException {

        String sql = """
                SELECT id, nombre, rut, correo, contraseña, rol
                FROM usuarios
                WHERE correo = ? AND contraseña = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, correo);
            statement.setString(2, contrasena);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return new Usuario(
                            result.getInt("id"),
                            result.getString("nombre"),
                            result.getString("rut"),
                            result.getString("correo"),
                            null,
                            result.getString("rol")
                    );
                }
            }
        }

        return null;
    }

    public Usuario buscarPorId(int id) throws SQLException {

        String sql = """
                SELECT id, nombre, rut, correo, rol
                FROM usuarios
                WHERE id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return new Usuario(
                            result.getInt("id"),
                            result.getString("nombre"),
                            result.getString("rut"),
                            result.getString("correo"),
                            null,
                            result.getString("rol")
                    );
                }
            }
        }

        return null;
    }
}
