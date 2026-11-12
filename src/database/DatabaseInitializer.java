package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void criarAdminPadrao() throws SQLException {

        String sql = """
                INSERT INTO usuarios
                    (nome, email, senha, tipo, estado)
                SELECT ?, ?, ?, ?, ?
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM usuarios
                    WHERE email = ?
                )
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, "Administrador");
            statement.setString(2, "admin@eventos.com");
            statement.setString(3, "Admin123");
            statement.setString(4, "ADMIN");
            statement.setBoolean(5, true);
            statement.setString(6, "admin@eventos.com");

            statement.executeUpdate();
        }
    }
}