package com.br.ccs.mark.version.on.ccsmark.dto;

import com.br.ccs.mark.version.on.ccsmark.model.Cliente;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;


public class ClienteDto {

    private Long id;
    private String nome;

    public ClienteDto(Cliente cliente) {
        this.id = cliente.getIdCliente();
        this.nome = cliente.getNome();
    }

    public static List<ClienteDto> converter(List<Cliente> contas) {
        return contas.stream().map(ClienteDto::new).collect(Collectors.toList());
    }

    public static Page<ClienteDto> converterPage(Page<Cliente> clientes) {
        return clientes.map(ClienteDto::new);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

}
