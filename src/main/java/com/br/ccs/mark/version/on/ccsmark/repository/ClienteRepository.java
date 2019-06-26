package com.br.ccs.mark.version.on.ccsmark.repository;

import com.br.ccs.mark.version.on.ccsmark.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Page<Cliente> findByNome(String nome, Pageable paginacao);
}