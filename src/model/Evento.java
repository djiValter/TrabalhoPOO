package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Evento {

    private int id;
    private String nome;
    private String descricao;
    private String local;
    private LocalDate data;
    private LocalTime hora;
    private int capacidade;
    private Promotor promotor;
    private boolean estado;

    public Evento(int id, String nome, String descricao, String local, LocalDate data, LocalTime hora, int capacidade, Promotor promotor, boolean estado) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.local = local;
        this.data = data;
        this.hora = hora;
        this.capacidade = capacidade;
        this.promotor = promotor;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLocal() {
        return local;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public Promotor getPromotor() {
        return promotor;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}