package model;

public abstract class Bilhete implements Validavel{

    private int id;
    private double preco;
    private int quantidade;
    private Evento evento;
    private boolean estado;

    public Bilhete(int id, double preco, int quantidade, Evento evento, boolean estado) {
        this.id = id;
        this.preco = preco;
        this.quantidade = quantidade;
        this.evento = evento;
        this.estado = estado;
    }

    @Override
    public boolean validar() {
        return false;
    }

    public abstract double calcularPrecoFinal();

    public int getId() {
        return id;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public Evento getEvento() {
        return evento;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}