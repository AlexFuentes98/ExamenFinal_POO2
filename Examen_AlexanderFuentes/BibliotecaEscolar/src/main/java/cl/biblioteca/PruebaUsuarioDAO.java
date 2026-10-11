
package cl.biblioteca;

import cl.biblioteca.dao.UsuarioDAO;
import cl.biblioteca.model.Usuario;

import java.sql.SQLException;
import java.util.Scanner;

public class PruebaUsuarioDAO {

    public static void main(String[] args) {

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("=== PRUEBA DE INICIO DE SESIÓN ===");

            System.out.print("Correo: ");
            String correo = scanner.nextLine();

            System.out.print("Contraseña: ");
            String contrasena = scanner.nextLine();

            Usuario usuario = usuarioDAO.autenticar(correo, contrasena);

            if (usuario != null) {
                System.out.println("¡Inicio de sesión correcto!");
                System.out.println("Usuario: " + usuario.getNombre());
                System.out.println("Rol: " + usuario.getRol());
            } else {
                System.out.println("Correo o contraseña incorrectos.");
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al consultar MySQL: " + e.getMessage()
            );
        }
    }
}
