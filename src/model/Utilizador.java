package model;

public abstract class Utilizador {

    private int id;
    private String nome;
    private String email;
    private String senha;
    private boolean estado;

    public Utilizador(int id, String nome, String email, String senha, boolean estado) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.estado = estado;
    }

    public abstract String getTipo();

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}