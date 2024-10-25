package com.project.model;

public class Departamento {
    private String nome;
    private Integer departamento_id;

    public Departamento() {
    }

    public Departamento(Departamento colunaDepartamento) {
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getDepartamento_id() {
        return departamento_id;
    }

    public void setDepartamento_id(Integer departamento_id) {
        this.departamento_id = departamento_id;
    }
}
