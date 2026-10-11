
package cl.biblioteca.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static DatabaseConnection instance;

    private static final String URL =
            "jdbc:mysql://localhost:3306/biblioteca";

    private static final String USER = "root";

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        String password = System.getenv("BIBLIOTECA_DB_PASSWORD");

        if (password == null) {
            throw new SQLException(
                    "Falta configurar la variable BIBLIOTECA_DB_PASSWORD"
            );
        }

        return DriverManager.getConnection(URL, USER, password);
    }
}
