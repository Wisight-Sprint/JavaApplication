package com.project.model;

public class Vitima {
    private Integer idade;
    private String etinia;
    private String genero;
    private String armamento;

    public Vitima(Integer idade, String etinia, String genero, String armamento) {
        this.idade = idade;
        this.etinia = etinia;
        this.genero = genero;
        this.armamento = armamento;

    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getEtinia() {
        return etinia;
    }

    public void setEtinia(String etinia) {
        this.etinia = etinia;
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
}
