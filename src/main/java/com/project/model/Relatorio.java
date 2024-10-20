package com.project.model;

import java.util.Date;

public class Relatorio {
    private Date dataOcorrencia;
    private String fuga;
    private Boolean cameraCorporal;
    private Boolean problemasMentais;
    private Integer relatorio_id;

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

    public Boolean getProblemasMentais() {
        return problemasMentais;
    }

    public void setProblemasMentais(Boolean problemasMentais) {
        this.problemasMentais = problemasMentais;
    }

    public Integer getRelatorio_id() {
        return relatorio_id;
    }

    public void setRelatorio_id(Integer relatorio_id) {
        this.relatorio_id = relatorio_id;
    }
}