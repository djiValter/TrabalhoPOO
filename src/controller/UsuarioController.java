package controller;

import model.Utilizador;
import service.UsuarioService;

import java.util.List;

public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController() {
        this.usuarioService =
                new UsuarioService();
    }

    public boolean criarCliente(
            String nome,
            String email,
            String senha
    ) {

        return usuarioService.criarCliente(
                nome,
                email,
                senha
        );
    }

    public boolean criarPromotor(
            String nome,
            String email,
            String senha
    ) {

        return usuarioService.criarPromotor(
                nome,
                email,
                senha
        );
    }

    public boolean criarAdmin(
            String nome,
            String email,
            String senha
    ) {

        return usuarioService.criarAdmin(
                nome,
                email,
                senha
        );
    }

    public Utilizador buscarPorId(int id) {

        return usuarioService.buscarPorId(id);
    }

    public List<Utilizador> listarTodos() {

        return usuarioService.listarTodos();
    }

    public boolean atualizar(
            Utilizador utilizador
    ) {

        return usuarioService.atualizar(
                utilizador
        );
    }

    public boolean alterarEstado(
            int id,
            boolean estado
    ) {

        return usuarioService.alterarEstado(
                id,
                estado
        );
    }

    public boolean eliminar(int id) {

        return usuarioService.eliminar(id);
    }
}