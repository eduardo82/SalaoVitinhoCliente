package br.com.eduardo.salaovitinhocliente.model;

public class Telefone {

    private String numero;
    private Boolean autorizado;

    public Telefone() {
    }

    public Telefone(String numero, Boolean autorizado) {
        this.numero = numero;
        this.autorizado = autorizado;
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
