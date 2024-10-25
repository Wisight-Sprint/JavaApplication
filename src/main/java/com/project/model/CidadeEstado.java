package com.project.model;

public class CidadeEstado {
    private String cidade;
    private String estado;
    private Integer cidade_estado_id;

    public CidadeEstado() {
    }

    public CidadeEstado(CidadeEstado colunaCidadeEstado) {
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCidade_estado_id() {
        return cidade_estado_id;
    }

    public void setCidade_estado_id(Integer cidade_estado_id) {
        this.cidade_estado_id = cidade_estado_id;
    }
}
