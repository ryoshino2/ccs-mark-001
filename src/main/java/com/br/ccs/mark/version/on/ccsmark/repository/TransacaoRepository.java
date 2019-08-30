package com.br.ccs.mark.version.on.ccsmark.repository;

import com.br.ccs.mark.version.on.ccsmark.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByIdContaCliente(Long idContaCliente);
}
