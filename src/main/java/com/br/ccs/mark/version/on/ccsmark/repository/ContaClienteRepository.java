package com.br.ccs.mark.version.on.ccsmark.repository;

import com.br.ccs.mark.version.on.ccsmark.model.ContaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ContaClienteRepository extends JpaRepository<ContaCliente, Long> {

    List<ContaCliente> findByDataAtualizacao(Date dataAtualizacao);
    long countByDataAtualizacao(Date dataAtualizacao);

}