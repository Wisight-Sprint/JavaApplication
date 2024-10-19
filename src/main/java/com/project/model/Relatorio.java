package com.project.model;

public class Relatorio {
    private String dataOcorrencia;
    private String fuga;
    private String cameraCorporal;
    private String problemasMentais;
    private Integer relatorio_id;

    public String getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(String dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public String getFuga() {
        return fuga;
    }

    public void setFuga(String fuga) {
        this.fuga = fuga;
    }

    public String getCameraCorporal() {
        return cameraCorporal;
    }

    public void setCameraCorporal(String cameraCorporal) {
        this.cameraCorporal = cameraCorporal;
    }

    public String getProblemasMentais() {
        return problemasMentais;
    }

    public void setProblemasMentais(String problemasMentais) {
        this.problemasMentais = problemasMentais;
    }

    public Integer getRelatorio_id() {
        return relatorio_id;
    }

    public void setRelatorio_id(Integer relatorio_id) {
        this.relatorio_id = relatorio_id;
    }
}
