package service;

import dao.UsuarioDAO;
import model.Utilizador;

public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Utilizador login(
            String email,
            String senha
    ) {

        if (email == null || email.isBlank()) {
            return null;
        }

        if (senha == null || senha.isBlank()) {
            return null;
        }

        return usuarioDAO.autenticar(
                email.trim(),
                senha
        );
    }
}