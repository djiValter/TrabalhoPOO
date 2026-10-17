package controller;

import model.Evento;
import service.EventoService;

import java.util.List;

public class EventoController {

    private final EventoService eventoService;

    public EventoController() {
        this.eventoService =
                new EventoService();
    }

    public boolean criar(Evento evento) {

        return eventoService.criar(
                evento
        );
    }

    public Evento buscarPorId(int id) {

        return eventoService.buscarPorId(
                id
        );
    }

    public List<Evento> listarTodos() {

        return eventoService.listarTodos();
    }

    public List<Evento> listarPorPromotor(
            int promotorId
    ) {

        return eventoService.listarPorPromotor(
                promotorId
        );
    }

    public boolean atualizar(Evento evento) {

        return eventoService.atualizar(
                evento
        );
    }

    public boolean alterarEstado(
            int id,
            boolean estado
    ) {

        return eventoService.alterarEstado(
                id,
                estado
        );
    }

    public boolean eliminar(int id) {

        return eventoService.eliminar(id);
    }
}