package com.br.ccs.mark.version.on.ccsmark.controller;

import com.br.ccs.mark.version.on.ccsmark.dto.ClienteDto;
import com.br.ccs.mark.version.on.ccsmark.dto.ContaClienteDto;
import com.br.ccs.mark.version.on.ccsmark.model.Cliente;
import com.br.ccs.mark.version.on.ccsmark.model.ContaCliente;
import com.br.ccs.mark.version.on.ccsmark.repository.ClienteRepository;
import com.br.ccs.mark.version.on.ccsmark.service.CcsService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@RestController
public class CcsController {

    @Autowired
    private CcsService ccsService;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("/listaCliente")
    @Cacheable(value = "cliente")
    public Iterable<Cliente> obterClientes() {
        return ccsService.obterTodosClientes();
    }

    @GetMapping("/listaContas")
    public Iterable<ContaCliente> obterContas() {
        return ccsService.obterTodasContas();
    }

    @GetMapping("/gerarRelatorio/{dataAtualizacao}")
    public ResponseEntity<List<ContaCliente>> gerarRelatorio(@PathVariable("dataAtualizacao") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataAtualizacao) throws IOException {
        List<ContaCliente> contaClienteList = ccsService.pesquisarPorData(dataAtualizacao);
        ccsService.escreverArquivo(contaClienteList, dataAtualizacao);
        return ResponseEntity.status(HttpStatus.OK).body(contaClienteList);
    }

    @GetMapping("/gerarRelatorio")
    public ResponseEntity<List<ContaCliente>> gerarRelatorioDiario() throws IOException {
        List<ContaCliente> contaClienteList = ccsService.pesquisarPorData(new Date());
        ccsService.escreverArquivo(contaClienteList, new Date());
        return ResponseEntity.status(HttpStatus.OK).body(contaClienteList);
    }

    @GetMapping("/gerarRelatorioCompleto")
    public ResponseEntity<List<ContaCliente>> gerarRelatorioCompleto() throws IOException {
        List<ContaCliente> contaClienteList = (List<ContaCliente>) ccsService.obterTodasContas();
        ccsService.escreverArquivoTodo(contaClienteList);
        return ResponseEntity.status(HttpStatus.OK).body(contaClienteList);
    }

    @GetMapping("/clientedto")
    @Cacheable(value = "clientedto")
    public List<ClienteDto> lista() {
        Iterable<Cliente> clienteDtos = obterClientes();
        return ClienteDto.converter((List<Cliente>) clienteDtos);
    }

    @GetMapping("/contaclientedto")
    @Cacheable(value = "contaclientedto")
    public List<ContaClienteDto> listaconta() {
        Iterable<ContaCliente> clienteDtos = obterContas();
        return ContaClienteDto.converter((List<ContaCliente>) clienteDtos);
    }

    @GetMapping("/enviarParaOKafka")
    public void producerKafka() {
        ccsService.enviarPeloKafka();
    }
}