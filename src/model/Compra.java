package model;

import java.time.LocalDateTime;

public class Compra {

    private int id;
    private Cliente cliente;
    private Bilhete bilhete;
    private int quantidade;
    private double valorTotal;
    private LocalDateTime dataCompra;

    public Compra(int id, Cliente cliente, Bilhete bilhete, int quantidade, LocalDateTime dataCompra) {
        this.id = id;
        this.cliente = cliente;
        this.bilhete = bilhete;
        this.quantidade = quantidade;
        this.dataCompra = dataCompra;

        this.valorTotal =
                bilhete.calcularPrecoFinal() * quantidade;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Bilhete getBilhete() {
        return bilhete;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }
}