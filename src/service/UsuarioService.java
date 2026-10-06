package service;

import dao.UsuarioDAO;
import model.Admin;
import model.Cliente;
import model.Promotor;
import model.Utilizador;

import java.util.List;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // =====================================================
    // CRIAR CLIENTE
    // =====================================================

    public boolean criarCliente(
            String nome,
            String email,
            String senha
    ) {

        if (!dadosValidos(nome, email, senha)) {
            return false;
        }

        Cliente cliente =
                new Cliente(
                        0,
                        nome.trim(),
                        email.trim(),
                        senha,
                        true
                );

        return usuarioDAO.criar(cliente);
    }

    // =====================================================
    // CRIAR PROMOTOR
    // =====================================================

    public boolean criarPromotor(
            String nome,
            String email,
            String senha
    ) {

        if (!dadosValidos(nome, email, senha)) {
            return false;
        }

        Promotor promotor =
                new Promotor(
                        0,
                        nome.trim(),
                        email.trim(),
                        senha,
                        true
                );

        return usuarioDAO.criar(promotor);
    }

    // =====================================================
    // CRIAR ADMIN
    // =====================================================

    public boolean criarAdmin(
            String nome,
            String email,
            String senha
    ) {

        if (!dadosValidos(nome, email, senha)) {
            return false;
        }

        Admin admin =
                new Admin(
                        0,
                        nome.trim(),
                        email.trim(),
                        senha,
                        true
                );

        return usuarioDAO.criar(admin);
    }

    // =====================================================
    // BUSCAR
    // =====================================================

    public Utilizador buscarPorId(int id) {

        if (id <= 0) {
            return null;
        }

        return usuarioDAO.buscarPorId(id);
    }

    // =====================================================
    // LISTAR
    // =====================================================

    public List<Utilizador> listarTodos() {

        return usuarioDAO.listarTodos();
    }

    // =====================================================
    // ATUALIZAR
    // =====================================================

    public boolean atualizar(
            Utilizador utilizador
    ) {

        if (utilizador == null) {
            return false;
        }

        if (!dadosValidos(
                utilizador.getNome(),
                utilizador.getEmail(),
                utilizador.getSenha()
        )) {
            return false;
        }

        return usuarioDAO.atualizar(
                utilizador
        );
    }

    // =====================================================
    // ATIVAR / DESATIVAR
    // =====================================================

    public boolean alterarEstado(
            int id,
            boolean estado
    ) {

        if (id <= 0) {
            return false;
        }

        return usuarioDAO.alterarEstado(
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

        return usuarioDAO.eliminar(id);
    }

    // =====================================================
    // VALIDAÇÃO
    // =====================================================

    private boolean dadosValidos(
            String nome,
            String email,
            String senha
    ) {

        return nome != null
                && !nome.isBlank()
                && email != null
                && !email.isBlank()
                && senha != null
                && !senha.isBlank();
    }
}