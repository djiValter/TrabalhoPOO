package model;

public class Admin extends Utilizador {

    public Admin(int id, String nome, String email, String senha, boolean estado) {
        super(id, nome, email, senha, estado);
    }

    @Override
    public String getTipo() {
        return "ADMIN";
    }
}