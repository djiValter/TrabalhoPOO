package controller;

import model.Utilizador;
import service.AuthService;

public class AuthController {

    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    public Utilizador login(
            String email,
            String senha
    ) {

        return authService.login(
                email,
                senha
        );
    }
}