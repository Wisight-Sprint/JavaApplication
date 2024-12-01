package com.project.model;

public class CidadeEstado extends Tratativa{
    private String cidade;
    private String estado;
    private Integer cidade_estado_id;
    private final CidadeEstado colunaCidadeEstado = new CidadeEstado();

    public CidadeEstado() {}

    public CidadeEstado(CidadeEstado colunaCidadeEstado) {}

    @Override
    public String tratativaDados() {
        return null;
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
