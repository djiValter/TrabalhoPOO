package model;


public class BilheteNormal extends Bilhete {

    public BilheteNormal(int id, double preco, int quantidade, Evento evento, boolean estado) {
        super(id, preco, quantidade, evento, estado);
    }

    @Override
    public double calcularPrecoFinal() {
        return getPreco();
    }

    @Override
    public boolean validar() {

    }
}