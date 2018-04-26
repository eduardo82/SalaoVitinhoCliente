package br.com.eduardo.salaovitinhocliente.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by eduardo.vasconcelos on 25/10/2017.
 */

public class Horario implements Serializable {

    private String horaAtendimento;
    private String diaAtendimento;
    private String nome;
    private String telefone;
    private boolean disponivel;
    private boolean autorizado;
    private boolean recusado;
    private boolean verificado;
    private String motivo;
    private String profissional;

    public Horario() {
        super();
    }

    public Horario(String nome, String telefone, String profissional, String diaAtendimento, String horaAtendimento, boolean disponivel, boolean autorizado, boolean recusado, boolean verificado) {
        this.telefone = telefone;
        this.nome = nome;
        this.profissional = profissional;
        this.disponivel = disponivel;
        this.autorizado = autorizado;
        this.diaAtendimento = diaAtendimento;
        this.horaAtendimento = horaAtendimento;
        this.recusado = recusado;
        this.verificado = verificado;
    }

    public String getProfissional() {
        return profissional;
    }

    public void setProfissional(String profissional) {
        this.profissional = profissional;
    }

    public String getHoraAtendimento() {
        return horaAtendimento;
    }

    public void setHoraAtendimento(String horaAtendimento) {
        this.horaAtendimento = horaAtendimento;
    }

    public String getDiaAtendimento() {
        return diaAtendimento;
    }

    public void setDiaAtendimento(String diaAtendimento) {
        this.diaAtendimento = diaAtendimento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public boolean isRecusado() {
        return recusado;
    }

    public void setRecusado(boolean recusado) {
        this.recusado = recusado;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}