package dao;

import database.DatabaseConnection;
import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    public boolean criarCompra(
            Cliente cliente,
            Bilhete bilhete,
            int quantidade
    ) {

        if (quantidade <= 0) {
            return false;
        }

        String sqlBilhete = """
                SELECT preco, quantidade
                FROM bilhetes
                WHERE id = ?
                  AND estado = TRUE
                FOR UPDATE
                """;

        String sqlCompra = """
                INSERT INTO compras
                (
                    cliente_id,
                    bilhete_id,
                    quantidade,
                    valor_total
                )
                VALUES (?, ?, ?, ?)
                """;

        String sqlAtualizarStock = """
                UPDATE bilhetes
                SET quantidade = quantidade - ?
                WHERE id = ?
                """;

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            // Iniciar transação
            connection.setAutoCommit(false);

            // =================================================
            // 1. VERIFICAR O BILHETE E O STOCK
            // =================================================

            int stockAtual;
            double preco;

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(sqlBilhete)
            ) {

                statement.setInt(
                        1,
                        bilhete.getId()
                );

                ResultSet result =
                        statement.executeQuery();

                if (!result.next()) {

                    connection.rollback();
                    return false;
                }

                preco =
                        result.getDouble("preco");

                stockAtual =
                        result.getInt("quantidade");
            }


            if (stockAtual < quantidade) {

                connection.rollback();

                System.out.println(
                        "Stock insuficiente."
                );

                return false;
            }



            double valorTotal =
                    preco * quantidade;



            try (
                    PreparedStatement statement =
                            connection.prepareStatement(sqlCompra)
            ) {

                statement.setInt(
                        1,
                        cliente.getId()
                );

                statement.setInt(
                        2,
                        bilhete.getId()
                );

                statement.setInt(
                        3,
                        quantidade
                );

                statement.setDouble(
                        4,
                        valorTotal
                );

                statement.executeUpdate();
            }



            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    sqlAtualizarStock
                            )
            ) {

                statement.setInt(
                        1,
                        quantidade
                );

                statement.setInt(
                        2,
                        bilhete.getId()
                );

                statement.executeUpdate();
            }


            connection.commit();

            System.out.println(
                    "Compra realizada com sucesso."
            );

            return true;

        } catch (SQLException e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            e.printStackTrace();

            return false;

        } finally {

            if (connection != null) {

                try {

                    connection.setAutoCommit(true);
                    connection.close();

                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
    }


    public Compra buscarPorId(int id) {

        String sql = """
                SELECT
                    c.id,
                    c.quantidade,
                    c.valor_total,
                    c.data_compra,

                    u.id AS cliente_id,
                    u.nome AS cliente_nome,
                    u.email AS cliente_email,
                    u.senha AS cliente_senha,
                    u.estado AS cliente_estado,

                    b.id AS bilhete_id,
                    b.tipo AS bilhete_tipo,
                    b.preco AS bilhete_preco,
                    b.quantidade AS bilhete_quantidade,
                    b.estado AS bilhete_estado,

                    e.id AS evento_id,
                    e.nome AS evento_nome,
                    e.descricao AS evento_descricao,
                    e.local AS evento_local,
                    e.data AS evento_data,
                    e.hora AS evento_hora,
                    e.capacidade AS evento_capacidade,
                    e.estado AS evento_estado

                FROM compras c

                JOIN usuarios u
                    ON u.id = c.cliente_id

                JOIN bilhetes b
                    ON b.id = c.bilhete_id

                JOIN eventos e
                    ON e.id = b.evento_id

                WHERE c.id = ?
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

                return transformarCompra(result);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    public List<Compra> listarTodos() {

        List<Compra> compras =
                new ArrayList<>();

        String sql = """
                SELECT
                    c.id,
                    c.quantidade,
                    c.valor_total,
                    c.data_compra,

                    u.id AS cliente_id,
                    u.nome AS cliente_nome,
                    u.email AS cliente_email,
                    u.senha AS cliente_senha,
                    u.estado AS cliente_estado,

                    b.id AS bilhete_id,
                    b.tipo AS bilhete_tipo,
                    b.preco AS bilhete_preco,
                    b.quantidade AS bilhete_quantidade,
                    b.estado AS bilhete_estado,

                    e.id AS evento_id,
                    e.nome AS evento_nome,
                    e.descricao AS evento_descricao,
                    e.local AS evento_local,
                    e.data AS evento_data,
                    e.hora AS evento_hora,
                    e.capacidade AS evento_capacidade,
                    e.estado AS evento_estado

                FROM compras c

                JOIN usuarios u
                    ON u.id = c.cliente_id

                JOIN bilhetes b
                    ON b.id = c.bilhete_id

                JOIN eventos e
                    ON e.id = b.evento_id

                ORDER BY c.data_compra DESC
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

                compras.add(
                        transformarCompra(result)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return compras;
    }

    public List<Compra> listarPorCliente(
            int clienteId
    ) {

        List<Compra> compras =
                new ArrayList<>();

        String sql = """
                SELECT
                    c.id,
                    c.quantidade,
                    c.valor_total,
                    c.data_compra,

                    u.id AS cliente_id,
                    u.nome AS cliente_nome,
                    u.email AS cliente_email,
                    u.senha AS cliente_senha,
                    u.estado AS cliente_estado,

                    b.id AS bilhete_id,
                    b.tipo AS bilhete_tipo,
                    b.preco AS bilhete_preco,
                    b.quantidade AS bilhete_quantidade,
                    b.estado AS bilhete_estado,

                    e.id AS evento_id,
                    e.nome AS evento_nome,
                    e.descricao AS evento_descricao,
                    e.local AS evento_local,
                    e.data AS evento_data,
                    e.hora AS evento_hora,
                    e.capacidade AS evento_capacidade,
                    e.estado AS evento_estado

                FROM compras c

                JOIN usuarios u
                    ON u.id = c.cliente_id

                JOIN bilhetes b
                    ON b.id = c.bilhete_id

                JOIN eventos e
                    ON e.id = b.evento_id

                WHERE c.cliente_id = ?

                ORDER BY c.data_compra DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, clienteId);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                compras.add(
                        transformarCompra(result)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return compras;
    }

    // =====================================================
    // VENDAS DOS EVENTOS DE UM PROMOTOR
    // =====================================================

    public List<Compra> listarVendasPorPromotor(
            int promotorId
    ) {

        List<Compra> compras =
                new ArrayList<>();

        String sql = """
                SELECT
                    c.id,
                    c.quantidade,
                    c.valor_total,
                    c.data_compra,

                    u.id AS cliente_id,
                    u.nome AS cliente_nome,
                    u.email AS cliente_email,
                    u.senha AS cliente_senha,
                    u.estado AS cliente_estado,

                    b.id AS bilhete_id,
                    b.tipo AS bilhete_tipo,
                    b.preco AS bilhete_preco,
                    b.quantidade AS bilhete_quantidade,
                    b.estado AS bilhete_estado,

                    e.id AS evento_id,
                    e.nome AS evento_nome,
                    e.descricao AS evento_descricao,
                    e.local AS evento_local,
                    e.data AS evento_data,
                    e.hora AS evento_hora,
                    e.capacidade AS evento_capacidade,
                    e.estado AS evento_estado

                FROM compras c

                JOIN usuarios u
                    ON u.id = c.cliente_id

                JOIN bilhetes b
                    ON b.id = c.bilhete_id

                JOIN eventos e
                    ON e.id = b.evento_id

                WHERE e.promotor_id = ?

                ORDER BY c.data_compra DESC
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

                compras.add(
                        transformarCompra(result)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return compras;
    }


    public boolean eliminar(int id) {

        String sql = """
                DELETE FROM compras
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


    private Compra transformarCompra(
            ResultSet result
    ) throws SQLException {

        Cliente cliente =
                new Cliente(
                        result.getInt("cliente_id"),
                        result.getString("cliente_nome"),
                        result.getString("cliente_email"),
                        result.getString("cliente_senha"),
                        result.getBoolean("cliente_estado")
                );

        Evento evento =
                new Evento(
                        result.getInt("evento_id"),
                        result.getString("evento_nome"),
                        result.getString("evento_descricao"),
                        result.getString("evento_local"),
                        result.getDate("evento_data")
                                .toLocalDate(),
                        result.getTime("evento_hora")
                                .toLocalTime(),
                        result.getInt("evento_capacidade"),
                        null,
                        result.getBoolean("evento_estado")
                );

        Bilhete bilhete;

        String tipo =
                result.getString("bilhete_tipo");

        if ("VIP".equals(tipo)) {

            bilhete =
                    new BilheteVIP(
                            result.getInt("bilhete_id"),
                            result.getDouble("bilhete_preco"),
                            result.getInt(
                                    "bilhete_quantidade"
                            ),
                            evento,
                            result.getBoolean(
                                    "bilhete_estado"
                            )
                    );

        } else {

            bilhete =
                    new BilheteNormal(
                            result.getInt("bilhete_id"),
                            result.getDouble("bilhete_preco"),
                            result.getInt(
                                    "bilhete_quantidade"
                            ),
                            evento,
                            result.getBoolean(
                                    "bilhete_estado"
                            )
                    );
        }

        LocalDateTime dataCompra =
                result.getTimestamp("data_compra")
                        .toLocalDateTime();

        Compra compra =
                new Compra(
                        result.getInt("id"),
                        cliente,
                        bilhete,
                        result.getInt("quantidade"),
                        dataCompra
                );

        return compra;
    }
}