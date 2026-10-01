package dao;

import database.DatabaseConnection;
import model.Evento;
import model.Promotor;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {


    public boolean criar(Evento evento) {

        String sql = """
                INSERT INTO eventos
                (
                    nome,
                    descricao,
                    local,
                    data,
                    hora,
                    capacidade,
                    promotor_id,
                    estado
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, evento.getNome());
            statement.setString(2, evento.getDescricao());
            statement.setString(3, evento.getLocal());
            statement.setDate(
                    4,
                    Date.valueOf(evento.getData())
            );
            statement.setTime(
                    5,
                    Time.valueOf(evento.getHora())
            );
            statement.setInt(6, evento.getCapacidade());
            statement.setInt(
                    7,
                    evento.getPromotor().getId()
            );
            statement.setBoolean(8, evento.isEstado());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }


    public Evento buscarPorId(int id) {

        String sql = """
                SELECT
                    e.id,
                    e.nome,
                    e.descricao,
                    e.local,
                    e.data,
                    e.hora,
                    e.capacidade,
                    e.estado,

                    u.id AS promotor_id,
                    u.nome AS promotor_nome,
                    u.email AS promotor_email,
                    u.senha AS promotor_senha,
                    u.estado AS promotor_estado

                FROM eventos e

                JOIN usuarios u
                    ON u.id = e.promotor_id

                WHERE e.id = ?
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

                return transformarEvento(result);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    public List<Evento> listarTodos() {

        List<Evento> eventos = new ArrayList<>();

        String sql = """
                SELECT
                    e.id,
                    e.nome,
                    e.descricao,
                    e.local,
                    e.data,
                    e.hora,
                    e.capacidade,
                    e.estado,

                    u.id AS promotor_id,
                    u.nome AS promotor_nome,
                    u.email AS promotor_email,
                    u.senha AS promotor_senha,
                    u.estado AS promotor_estado

                FROM eventos e

                JOIN usuarios u
                    ON u.id = e.promotor_id

                ORDER BY e.data, e.hora
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

                eventos.add(
                        transformarEvento(result)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return eventos;
    }


    public List<Evento> listarPorPromotor(int promotorId) {

        List<Evento> eventos = new ArrayList<>();

        String sql = """
                SELECT
                    e.id,
                    e.nome,
                    e.descricao,
                    e.local,
                    e.data,
                    e.hora,
                    e.capacidade,
                    e.estado,

                    u.id AS promotor_id,
                    u.nome AS promotor_nome,
                    u.email AS promotor_email,
                    u.senha AS promotor_senha,
                    u.estado AS promotor_estado

                FROM eventos e

                JOIN usuarios u
                    ON u.id = e.promotor_id

                WHERE e.promotor_id = ?

                ORDER BY e.data, e.hora
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, promotorId);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                eventos.add(
                        transformarEvento(result)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return eventos;
    }


    public boolean atualizar(Evento evento) {

        String sql = """
                UPDATE eventos

                SET
                    nome = ?,
                    descricao = ?,
                    local = ?,
                    data = ?,
                    hora = ?,
                    capacidade = ?,
                    estado = ?

                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, evento.getNome());
            statement.setString(2, evento.getDescricao());
            statement.setString(3, evento.getLocal());
            statement.setDate(
                    4,
                    Date.valueOf(evento.getData())
            );
            statement.setTime(
                    5,
                    Time.valueOf(evento.getHora())
            );
            statement.setInt(6, evento.getCapacidade());
            statement.setBoolean(7, evento.isEstado());
            statement.setInt(8, evento.getId());

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
                UPDATE eventos
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
                DELETE FROM eventos
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


    private Evento transformarEvento(
            ResultSet result
    ) throws SQLException {

        Promotor promotor =
                new Promotor(
                        result.getInt("promotor_id"),
                        result.getString("promotor_nome"),
                        result.getString("promotor_email"),
                        result.getString("promotor_senha"),
                        result.getBoolean("promotor_estado")
                );

        LocalDate data =
                result.getDate("data").toLocalDate();

        LocalTime hora =
                result.getTime("hora").toLocalTime();

        return new Evento(
                result.getInt("id"),
                result.getString("nome"),
                result.getString("descricao"),
                result.getString("local"),
                data,
                hora,
                result.getInt("capacidade"),
                promotor,
                result.getBoolean("estado")
        );
    }
}