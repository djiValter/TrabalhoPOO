package service;

import dao.CompraDAO;
import model.Bilhete;
import model.Cliente;
import model.Compra;

import java.util.List;

public class CompraService {

    private final CompraDAO compraDAO;

    public CompraService() {
        this.compraDAO = new CompraDAO();
    }

    // =====================================================
    // REALIZAR COMPRA
    // =====================================================

    public boolean comprar(
            Cliente cliente,
            Bilhete bilhete,
            int quantidade
    ) {

        if (cliente == null) {
            return false;
        }

        if (bilhete == null) {
            return false;
        }

        if (quantidade <= 0) {
            return false;
        }

        if (!cliente.isEstado()) {
            return false;
        }

        if (!bilhete.isEstado()) {
            return false;
        }

        return compraDAO.criarCompra(
                cliente,
                bilhete,
                quantidade
        );
    }

    // =====================================================
    // BUSCAR
    // =====================================================

    public Compra buscarPorId(int id) {

        if (id <= 0) {
            return null;
        }

        return compraDAO.buscarPorId(id);
    }

    // =====================================================
    // TODAS AS COMPRAS
    // =====================================================

    public List<Compra> listarTodos() {

        return compraDAO.listarTodos();
    }

    // =====================================================
    // COMPRAS DO CLIENTE
    // =====================================================

    public List<Compra> listarPorCliente(
            int clienteId
    ) {

        if (clienteId <= 0) {
            return List.of();
        }

        return compraDAO.listarPorCliente(
                clienteId
        );
    }

    // =====================================================
    // VENDAS DO PROMOTOR
    // =====================================================

    public List<Compra> listarVendasPorPromotor(
            int promotorId
    ) {

        if (promotorId <= 0) {
            return List.of();
        }

        return compraDAO.listarVendasPorPromotor(
                promotorId
        );
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    public boolean eliminar(int id) {

        if (id <= 0) {
            return false;
        }

        return compraDAO.eliminar(id);
    }
}