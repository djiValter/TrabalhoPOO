package dao;

import database.DatabaseConnection;
import model.Bilhete;
import model.BilheteNormal;
import model.BilheteVIP;
import model.Evento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BilheteDAO {
    public boolean criar(Bilhete bilhete) {

        String sql = """
                INSERT INTO bilhetes
                (
                    evento_id,
                    tipo,
                    preco,
                    quantidade,
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

            statement.setInt(
                    1,
                    bilhete.getEvento().getId()
            );

            statement.setString(
                    2,
                    obterTipo(bilhete)
            );

            statement.setDouble(
                    3,
                    bilhete.getPreco()
            );

            statement.setInt(
                    4,
                    bilhete.getQuantidade()
            );

            statement.setBoolean(
                    5,
                    bilhete.isEstado()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }


    public Bilhete buscarPorId(int id) {

        String sql = """
                SELECT
                    b.id,
                    b.tipo,
                    b.preco,
                    b.quantidade,
                    b.estado,

                    e.id AS evento_id,
                    e.nome AS evento_nome,
                    e.descricao AS evento_descricao,
                    e.local AS evento_local,
                    e.data AS evento_data,
                    e.hora AS evento_hora,
                    e.capacidade AS evento_capacidade,
                    e.estado AS evento_estado,
                    e.promotor_id

                FROM bilhetes b

                JOIN eventos e
                    ON e.id = b.evento_id

                WHERE b.id = ?
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

                return transformarBilhete(result);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    public List<Bilhete> listarTodos() {

        List<Bilhete> bilhetes = new ArrayList<>();

        String sql = """
                SELECT
                    b.id,
                    b.tipo,
                    b.preco,
                    b.quantidade,
                    b.estado,

                    e.id AS evento_id,
                    e.nome AS evento_nome,
                    e.descricao AS evento_descricao,
                    e.local AS evento_local,
                    e.data AS evento_data,
                    e.hora AS evento_hora,
                    e.capacidade AS evento_capacidade,
                    e.estado AS evento_estado,
                    e.promotor_id

                FROM bilhetes b

                JOIN eventos e
                    ON e.id = b.evento_id

                ORDER BY e.data, b.tipo
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

                bilhetes.add(
                        transformarBilhete(result)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return bilhetes;
    }


    public List<Bilhete> listarPorEvento(int eventoId) {

        List<Bilhete> bilhetes = new ArrayList<>();

        String sql = """
                SELECT
                    b.id,
                    b.tipo,
                    b.preco,
                    b.quantidade,
                    b.estado,

                    e.id AS evento_id,
                    e.nome AS evento_nome,
                    e.descricao AS evento_descricao,
                    e.local AS evento_local,
                    e.data AS evento_data,
                    e.hora AS evento_hora,
                    e.capacidade AS evento_capacidade,
                    e.estado AS evento_estado,
                    e.promotor_id

                FROM bilhetes b

                JOIN eventos e
                    ON e.id = b.evento_id

                WHERE b.evento_id = ?

                ORDER BY b.tipo
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, eventoId);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                bilhetes.add(
                        transformarBilhete(result)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return bilhetes;
    }


    public boolean atualizar(Bilhete bilhete) {

        String sql = """
                UPDATE bilhetes

                SET
                    tipo = ?,
                    preco = ?,
                    quantidade = ?,
                    estado = ?

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
                    obterTipo(bilhete)
            );

            statement.setDouble(
                    2,
                    bilhete.getPreco()
            );

            statement.setInt(
                    3,
                    bilhete.getQuantidade()
            );

            statement.setBoolean(
                    4,
                    bilhete.isEstado()
            );

            statement.setInt(
                    5,
                    bilhete.getId()
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
                UPDATE bilhetes
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
                DELETE FROM bilhetes
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


    private String obterTipo(Bilhete bilhete) {

        if (bilhete instanceof BilheteVIP) {
            return "VIP";
        }

        return "NORMAL";
    }


    private Bilhete transformarBilhete(
            ResultSet result
    ) throws SQLException {

        Evento evento = new Evento(
                result.getInt("evento_id"),
                result.getString("evento_nome"),
                result.getString("evento_descricao"),
                result.getString("evento_local"),
                result.getDate("evento_data").toLocalDate(),
                result.getTime("evento_hora").toLocalTime(),
                result.getInt("evento_capacidade"),
                null,
                result.getBoolean("evento_estado")
        );

        String tipo = result.getString("tipo");

        if ("VIP".equals(tipo)) {

            return new BilheteVIP(
                    result.getInt("id"),
                    result.getDouble("preco"),
                    result.getInt("quantidade"),
                    evento,
                    result.getBoolean("estado")
            );
        }

        return new BilheteNormal(
                result.getInt("id"),
                result.getDouble("preco"),
                result.getInt("quantidade"),
                evento,
                result.getBoolean("estado")
        );
    }
}