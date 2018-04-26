package br.com.eduardo.salaovitinhocliente.model;

import com.google.firebase.database.Exclude;

/**
 * Created by eduardo.vasconcelos on 01/11/2017.
 */

public class Mensagem {

    @Exclude
    private Long id;
    private String mensagem;
    private String remetente;
    private String telefone;
    private String resposta;
    private boolean lido;

    public Mensagem() {
        super();
    }

    public Mensagem(String mensagem, String nome, String telefone, String resposta, boolean lido) {
        this.mensagem = mensagem;
        this.remetente = nome;
        this.telefone = telefone;
        this.resposta = resposta;
        this.lido = lido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public boolean isLido() {
        return lido;
    }

    public void setLido(boolean lido) {
        this.lido = lido;
    }
}
