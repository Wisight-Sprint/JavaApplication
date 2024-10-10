package src.main.java;

public class Usuario {
    private String nome;
    private String email;
    private Integer distintivo;
    private String cargo;
    private String senha;

    public Usuario(String nome, String email, Integer distintivo, String cargo, String senha) {
        this.nome = nome;
        this.email = email;
        this.distintivo = distintivo;
        this.cargo = cargo;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDistintivo() {
        return distintivo;
    }

    public void setDistintivo(Integer distintivo) {
        this.distintivo = distintivo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
