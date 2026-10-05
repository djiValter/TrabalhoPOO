package service;

import dao.BilheteDAO;
import model.Bilhete;
import model.BilheteNormal;
import model.BilheteVIP;
import model.Evento;

import java.util.List;

public class BilheteService {

    private final BilheteDAO bilheteDAO;

    public BilheteService() {
        this.bilheteDAO = new BilheteDAO();
    }

    // =====================================================
    // CRIAR BILHETE NORMAL
    // =====================================================

    public boolean criarNormal(
            double preco,
            int quantidade,
            Evento evento
    ) {

        if (!dadosValidos(
                preco,
                quantidade,
                evento
        )) {
            return false;
        }

        BilheteNormal bilhete =
                new BilheteNormal(
                        0,
                        preco,
                        quantidade,
                        evento,
                        true
                );

        return bilheteDAO.criar(bilhete);
    }

    // =====================================================
    // CRIAR BILHETE VIP
    // =====================================================

    public boolean criarVIP(
            double preco,
            int quantidade,
            Evento evento
    ) {

        if (!dadosValidos(
                preco,
                quantidade,
                evento
        )) {
            return false;
        }

        BilheteVIP bilhete =
                new BilheteVIP(
                        0,
                        preco,
                        quantidade,
                        evento,
                        true
                );

        return bilheteDAO.criar(bilhete);
    }

    // =====================================================
    // BUSCAR
    // =====================================================

    public Bilhete buscarPorId(int id) {

        if (id <= 0) {
            return null;
        }

        return bilheteDAO.buscarPorId(id);
    }

    // =====================================================
    // LISTAR TODOS
    // =====================================================

    public List<Bilhete> listarTodos() {

        return bilheteDAO.listarTodos();
    }

    // =====================================================
    // LISTAR POR EVENTO
    // =====================================================

    public List<Bilhete> listarPorEvento(
            int eventoId
    ) {

        if (eventoId <= 0) {
            return List.of();
        }

        return bilheteDAO.listarPorEvento(
                eventoId
        );
    }

    // =====================================================
    // ATUALIZAR
    // =====================================================

    public boolean atualizar(Bilhete bilhete) {

        if (bilhete == null
                || bilhete.getId() <= 0) {
            return false;
        }

        if (bilhete.getPreco() < 0) {
            return false;
        }

        if (bilhete.getQuantidade() < 0) {
            return false;
        }

        return bilheteDAO.atualizar(
                bilhete
        );
    }

    // =====================================================
    // ALTERAR ESTADO
    // =====================================================

    public boolean alterarEstado(
            int id,
            boolean estado
    ) {

        if (id <= 0) {
            return false;
        }

        return bilheteDAO.alterarEstado(
                id,
                estado
        );
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    public boolean eliminar(int id) {

        if (id <= 0) {
            return false;
        }

        return bilheteDAO.eliminar(id);
    }

    // =====================================================
    // VALIDAÇÃO
    // =====================================================

    private boolean dadosValidos(
            double preco,
            int quantidade,
            Evento evento
    ) {

        return preco >= 0
                && quantidade >= 0
                && evento != null
                && evento.getId() > 0;
    }
}