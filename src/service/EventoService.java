package service;

import dao.EventoDAO;
import model.Evento;

import java.time.LocalDate;
import java.util.List;

public class EventoService {

    private final EventoDAO eventoDAO;

    public EventoService() {
        this.eventoDAO = new EventoDAO();
    }

    // =====================================================
    // CRIAR
    // =====================================================

    public boolean criar(Evento evento) {

        if (evento == null) {
            return false;
        }

        if (evento.getNome() == null
                || evento.getNome().isBlank()) {
            return false;
        }

        if (evento.getLocal() == null
                || evento.getLocal().isBlank()) {
            return false;
        }

        if (evento.getData() == null
                || evento.getData().isBefore(LocalDate.now())) {
            return false;
        }

        if (evento.getHora() == null) {
            return false;
        }

        if (evento.getCapacidade() <= 0) {
            return false;
        }

        if (evento.getPromotor() == null) {
            return false;
        }

        return eventoDAO.criar(evento);
    }

    // =====================================================
    // BUSCAR
    // =====================================================

    public Evento buscarPorId(int id) {

        if (id <= 0) {
            return null;
        }

        return eventoDAO.buscarPorId(id);
    }

    // =====================================================
    // LISTAR TODOS
    // =====================================================

    public List<Evento> listarTodos() {

        return eventoDAO.listarTodos();
    }

    // =====================================================
    // EVENTOS DO PROMOTOR
    // =====================================================

    public List<Evento> listarPorPromotor(
            int promotorId
    ) {

        if (promotorId <= 0) {
            return List.of();
        }

        return eventoDAO.listarPorPromotor(
                promotorId
        );
    }

    // =====================================================
    // ATUALIZAR
    // =====================================================

    public boolean atualizar(Evento evento) {

        if (evento == null
                || evento.getId() <= 0) {
            return false;
        }

        if (evento.getNome() == null
                || evento.getNome().isBlank()) {
            return false;
        }

        if (evento.getCapacidade() <= 0) {
            return false;
        }

        return eventoDAO.atualizar(evento);
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

        return eventoDAO.alterarEstado(
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

        return eventoDAO.eliminar(id);
    }
}