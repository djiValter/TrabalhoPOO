package controller;

import model.Bilhete;
import model.Evento;
import service.BilheteService;

import java.util.List;

public class BilheteController {

    private final BilheteService bilheteService;

    public BilheteController() {
        this.bilheteService =
                new BilheteService();
    }

    public boolean criarNormal(
            double preco,
            int quantidade,
            Evento evento
    ) {

        return bilheteService.criarNormal(
                preco,
                quantidade,
                evento
        );
    }

    public boolean criarVIP(
            double preco,
            int quantidade,
            Evento evento
    ) {

        return bilheteService.criarVIP(
                preco,
                quantidade,
                evento
        );
    }

    public Bilhete buscarPorId(int id) {

        return bilheteService.buscarPorId(
                id
        );
    }

    public List<Bilhete> listarTodos() {

        return bilheteService.listarTodos();
    }

    public List<Bilhete> listarPorEvento(
            int eventoId
    ) {

        return bilheteService.listarPorEvento(
                eventoId
        );
    }

    public boolean atualizar(
            Bilhete bilhete
    ) {

        return bilheteService.atualizar(
                bilhete
        );
    }

    public boolean alterarEstado(
            int id,
            boolean estado
    ) {

        return bilheteService.alterarEstado(
                id,
                estado
        );
    }

    public boolean eliminar(int id) {

        return bilheteService.eliminar(id);
    }
}