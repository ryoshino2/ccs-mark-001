package com.br.ccs.mark.version.on.ccsmark.service;

import com.br.ccs.mark.version.on.ccsmark.model.Cliente;
import com.br.ccs.mark.version.on.ccsmark.model.ContaCliente;
import com.br.ccs.mark.version.on.ccsmark.model.TipoTransacao;
import com.br.ccs.mark.version.on.ccsmark.model.Transacao;
import com.br.ccs.mark.version.on.ccsmark.repository.ClienteRepository;
import com.br.ccs.mark.version.on.ccsmark.repository.ContaClienteRepository;
import com.br.ccs.mark.version.on.ccsmark.repository.TransacaoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CcsServiceTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaClienteRepository contaClienteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;
    private CcsService ccsService;
    private Cliente cliente;
    private ContaCliente contaCliente;
    private Transacao transacao;

    @Before
    public void setup() throws ParseException {
        ccsService = new CcsService(ccsService);
        cliente = new Cliente("Rafael", "endereco", 123, "email@gmail.com", 7899, new Date());
        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
        contaCliente = new ContaCliente(cliente, 200.0, new Date());
        ccsService.saveTransaction(contaCliente);
        transacao = new Transacao(cliente.getIdCliente(), 20.0, new Date(), TipoTransacao.CREDIT);
    }

    @Test
    public void obterTodosClientes() {
        List listaClientes = (List) ccsService.obterTodosClientes();
        assertEquals(1, listaClientes.size());
    }

    @Test
    public void deveLocalizarContaAoPesquisarPorData() throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
        List listaClientesTotal = ccsService.pesquisarPorData(new Date());

        assertEquals(1, listaClientesTotal.size());
    }

    @Test
    public void naoDeveLocalizarAoPesquisarPorData() throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
        List listaClientesTotal = ccsService.pesquisarPorData(new Date());
        assertEquals(0, listaClientesTotal.size());
    }

    @Test
    public void atualizarSaldo() throws ParseException {
        Double saldo = null;
        saldo = contaCliente.getSaldoConta() + transacao.getValorTransacao();
        ccsService.atualizarSaldoContaCliente(contaCliente.getIdConta(), transacao, saldo);
        assertEquals(contaCliente.getSaldoConta(), 220.0, 0);
    }

    @Test
    public void buscarTransacoes() {
    }
}
