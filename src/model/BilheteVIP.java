
package model;

public class BilheteVIP extends Bilhete {

    public BilheteVIP(int id, double preco, int quantidade, Evento evento, boolean estado) {
        super(id, preco, quantidade, evento, estado);
    }

    @Override
    public double calcularPrecoFinal() {
        return getPreco() * 1.5;
    }

    @Override
    public boolean validar() {
        return getPreco() > 0 && getQuantidade() > 0;
    }
}
