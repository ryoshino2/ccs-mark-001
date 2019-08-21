 package com.br.ccs.mark.version.on.ccsmark.service;

 import com.br.ccs.mark.version.on.ccsmark.model.Cliente;
 import com.br.ccs.mark.version.on.ccsmark.model.ContaCliente;
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


     @Before
     public void setup() throws ParseException {
         ccsService = new CcsService(clienteRepository, contaClienteRepository, transacaoRepository);
         cliente = new Cliente("Rafael", "endereco", 123, "email@gmail.com", 7899, new Date());
         SimpleDateFormat formato = new SimpleDateFormat( "yyyy/MM/dd" );
         contaCliente = new ContaCliente(cliente, 200.0, formato.parse("2019/08/25"));
         ccsService.saveTransaction(contaCliente);
     }

     @Test
     public void obterTodosClientes() {
         List listaClientes = (List) ccsService.obterTodosClientes();
         assertEquals(1, listaClientes.size());
     }

     @Test
     public void deveLocalizarContaAoPesquisarPorData() throws ParseException {
         SimpleDateFormat formato = new SimpleDateFormat( "yyyy/MM/dd" );
         List listaClientesTotal = ccsService.pesquisarPorData(formato.parse("2019/08/25"));

         assertEquals(1, listaClientesTotal.size());
     }

     @Test
     public void naoDeveLocalizarAoPesquisarPorData() throws ParseException {
         SimpleDateFormat formato = new SimpleDateFormat( "yyyy/MM/dd" );
         List listaClientesTotal = ccsService.pesquisarPorData(formato.parse("2019/08/26"));
         assertEquals(0, listaClientesTotal.size());
     }

 }
