package com.br.ccs.mark.version.on.ccsmark.dto;

import com.br.ccs.mark.version.on.ccsmark.model.ContaCliente;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ContaClienteDto {

    private Long id;
    private Double saldoConta;
    private Date dataAtualizacao;

    public ContaClienteDto(ContaCliente contaCliente){
        this.id = contaCliente.getIdConta();
        this.saldoConta = contaCliente.getSaldoConta();
        this.dataAtualizacao = contaCliente.getDataAtualizacao();
    }

    public static List<ContaClienteDto> converter(List<ContaCliente> contas) {
        return contas.stream().map(ContaClienteDto::new).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public Double getSaldoConta() {
        return saldoConta;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }
}