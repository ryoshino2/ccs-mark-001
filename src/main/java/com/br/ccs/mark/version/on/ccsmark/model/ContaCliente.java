package com.br.ccs.mark.version.on.ccsmark.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ContaCliente {

    @Id
    @GeneratedValue
    private Integer idConta;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "clienteConta")
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Cliente idCliente;

    private Double saldoConta;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataAtualizacao;

    public ContaCliente() {
    }

    public ContaCliente(Cliente idCliente, Double saldoConta, Date dataAtualizacao) {
        this.idCliente = idCliente;
        this.saldoConta = saldoConta;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Integer getIdConta() {
        return idConta;
    }

    public void setIdConta(Integer idConta) {
        this.idConta = idConta;
    }

    public Cliente getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Cliente idCliente) {
        this.idCliente = idCliente;
    }

    public Double getSaldoConta() {
        return saldoConta;
    }

    public void setSaldoConta(Double saldoConta) {
        this.saldoConta = saldoConta;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Override
    public String toString() {
        return idCliente.getNome();
    }
}
