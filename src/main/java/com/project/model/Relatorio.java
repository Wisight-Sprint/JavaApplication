package com.project.model;

import java.util.Date;

public class Relatorio {
    private Date dataOcorrencia;
    private String fuga;
    private Boolean cameraCorporal;
    private Integer relatorio_id;

    public Relatorio() {
    }

    public Relatorio(Relatorio colunaRelatorio) {
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public String getFuga() {
        return fuga;
    }

    public void setFuga(String fuga) {
        this.fuga = fuga;
    }

    public Boolean getCameraCorporal() {
        return cameraCorporal;
    }

    public void setCameraCorporal(Boolean cameraCorporal) {
        this.cameraCorporal = cameraCorporal;
    }

    public Integer getRelatorio_id() {
        return relatorio_id;
    }

    public void setRelatorio_id(Integer relatorio_id) {
        this.relatorio_id = relatorio_id;
    }

    @Override
    public String toString() {
        return "Relatorio{" +
                "dataOcorrencia=" + dataOcorrencia +
                ", fuga='" + fuga + '\'' +
                ", cameraCorporal=" + cameraCorporal +
                ", relatorio_id=" + relatorio_id +
                '}';
    }
}
