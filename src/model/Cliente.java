package model;

public class Cliente extends Utilizador {

    public Cliente(int id, String nome, String email, String senha, boolean estado) {
        super(id, nome, email, senha, estado);
    }

    @Override
    public String getTipo() {
        return "CLIENTE";
    }
}