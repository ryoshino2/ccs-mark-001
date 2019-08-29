package com.br.ccs.mark.version.on.ccsmark.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class Transacao {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idTransacao;

    private Long idContaCliente;
    private Double valorTransacao;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataTransacao;
    private TipoTransacao tipoTransacao;



    public Transacao() {
    }

    public Transacao(Long idContaCliente, Double valorTransacao, Date dataTransacao, TipoTransacao tipoTransacao) {
        this.idContaCliente = idContaCliente;
        this.valorTransacao = valorTransacao;
        this.dataTransacao = dataTransacao;
        this.tipoTransacao = tipoTransacao;
    }


    public Long getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(Long idTransacao) {
        this.idTransacao = idTransacao;
    }

    public Double getValorTransacao() {
        return valorTransacao;
    }

    public void setValorTransacao(Double valorTransacao) {
        this.valorTransacao = valorTransacao;
    }

    public Date getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(Date dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }


    @Override
    public String toString() {
        return "Transacao{" +
                "id=" + idTransacao +
                ", valorTransacao=" + valorTransacao +
                ", dataTransacao=" + dataTransacao +
                ", tipoTransacao=" + tipoTransacao +
                '}';
    }

    public Long getIdContaCliente() {
        return idContaCliente;
    }

    public void setIdContaCliente(Long idContaCliente) {
        this.idContaCliente = idContaCliente;
    }
}
