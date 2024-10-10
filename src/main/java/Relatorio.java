package src.main.java;

public class Relatorio {
    private String dataOcorrencia;
    private String fuga;
    private Integer cameraCorporal;
    private String problemasMentais;

    public Relatorio(String dataOcorrencia, String fuga, Integer cameraCorporal, String problemasMentais) {
        this.dataOcorrencia = dataOcorrencia;
        this.fuga = fuga;
        this.cameraCorporal = cameraCorporal;
        this.problemasMentais = problemasMentais;
    }

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

    public Integer getCameraCorporal() {
        return cameraCorporal;
    }

    public void setCameraCorporal(Integer cameraCorporal) {
        this.cameraCorporal = cameraCorporal;
    }

    public String getProblemasMentais() {
        return problemasMentais;
    }

    public void setProblemasMentais(String problemasMentais) {
        this.problemasMentais = problemasMentais;
    }
}
