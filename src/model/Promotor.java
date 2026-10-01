package model;

public class Promotor extends Utilizador {

    public Promotor(int id, String nome, String email, String senha, boolean estado) {
        super(id, nome, email, senha, estado);
    }

    @Override
    public String getTipo() {
        return "PROMOTOR";
    }
}