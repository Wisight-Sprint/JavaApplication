package com.project.model;

public class Vitima extends Tratativa{
    private String nome;
    private Integer idade;
    private String etnia;
    private String genero;
    private String armamento;
    private Integer vitima_id;

    public Vitima() {}

    public Vitima(Vitima colunaVitima) {}

    @Override
    public String tratativaDados() {
        return null;
        
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getEtnia() {
        return etnia;
    }

    public void setEtnia(String etnia) {
        this.etnia = etnia;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getArmamento() {
        return armamento;
    }

    public void setArmamento(String armamento) {
        this.armamento = armamento;
    }

    public Integer getVitima_id() {
        return vitima_id;
    }

    public void setVitima_id(Integer vitima_id) {
        this.vitima_id = vitima_id;
    }
}