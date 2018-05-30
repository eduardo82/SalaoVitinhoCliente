package br.com.eduardo.salaovitinhocliente.model;

public class Telefone {

    private String nome;
    private String numero;
    private Boolean autorizado;
    private Boolean novo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getNovo() {
        return novo;
    }

    public void setNovo(Boolean novo) {
        this.novo = novo;
    }

    public Telefone() {
    }

    public Telefone(String nome, String numero, Boolean autorizado, Boolean novo) {
        this.nome = nome;
        this.numero = numero;
        this.autorizado = autorizado;
        this.novo = novo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Boolean getAutorizado() {
        return autorizado;
    }

    public void setAutorizado(Boolean autorizado) {
        this.autorizado = autorizado;
    }
}
