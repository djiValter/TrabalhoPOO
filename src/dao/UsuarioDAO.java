package dao;

import database.DatabaseConnection;
import model.Admin;
import model.Cliente;
import model.Promotor;
import model.Utilizador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Utilizador autenticar(
            String email,
            String senha
    ) {

        String sql = """
                SELECT id, nome, email, senha, tipo, estado
                FROM usuarios
                WHERE email = ?
                  AND senha = ?
                  AND estado = TRUE
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);
            statement.setString(2, senha);

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {
                return criarUtilizador(result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean criar(Utilizador utilizador) {

        String sql = """
                INSERT INTO usuarios
                (
                    nome,
                    email,
                    senha,
                    tipo,
                    estado
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    utilizador.getNome()
            );

            statement.setString(
                    2,
                    utilizador.getEmail()
            );

            statement.setString(
                    3,
                    utilizador.getSenha()
            );

            statement.setString(
                    4,
                    utilizador.getTipo()
            );

            statement.setBoolean(
                    5,
                    utilizador.isEstado()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }


    public Utilizador buscarPorId(int id) {

        String sql = """
                SELECT id, nome, email, senha, tipo, estado
                FROM usuarios
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {
                return criarUtilizador(result);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    public List<Utilizador> listarTodos() {

        List<Utilizador> utilizadores =
                new ArrayList<>();

        String sql = """
                SELECT id, nome, email, senha, tipo, estado
                FROM usuarios
                ORDER BY nome
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                utilizadores.add(
                        criarUtilizador(result)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return utilizadores;
    }



    public boolean atualizar(
            Utilizador utilizador
    ) {

        String sql = """
                UPDATE usuarios

                SET
                    nome = ?,
                    email = ?,
                    senha = ?

                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    utilizador.getNome()
            );

            statement.setString(
                    2,
                    utilizador.getEmail()
            );

            statement.setString(
                    3,
                    utilizador.getSenha()
            );

            statement.setInt(
                    4,
                    utilizador.getId()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }



    public boolean alterarEstado(
            int id,
            boolean estado
    ) {

        String sql = """
                UPDATE usuarios
                SET estado = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBoolean(1, estado);
            statement.setInt(2, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }



    public boolean eliminar(int id) {

        String sql = """
                DELETE FROM usuarios
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }



    private Utilizador criarUtilizador(
            ResultSet result
    ) throws SQLException {

        int id =
                result.getInt("id");

        String nome =
                result.getString("nome");

        String email =
                result.getString("email");

        String senha =
                result.getString("senha");

        String tipo =
                result.getString("tipo");

        boolean estado =
                result.getBoolean("estado");

        return switch (tipo) {

            case "CLIENTE" ->
                    new Cliente(
                            id,
                            nome,
                            email,
                            senha,
                            estado
                    );

            case "PROMOTOR" ->
                    new Promotor(
                            id,
                            nome,
                            email,
                            senha,
                            estado
                    );

            case "ADMIN" ->
                    new Admin(
                            id,
                            nome,
                            email,
                            senha,
                            estado
                    );

            default -> null;
        };
    }
}