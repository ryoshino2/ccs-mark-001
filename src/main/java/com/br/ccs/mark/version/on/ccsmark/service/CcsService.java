package com.br.ccs.mark.version.on.ccsmark.service;

import com.br.ccs.mark.version.on.ccsmark.model.Cliente;
import com.br.ccs.mark.version.on.ccsmark.model.ContaCliente;
import com.br.ccs.mark.version.on.ccsmark.model.TipoTransacao;
import com.br.ccs.mark.version.on.ccsmark.model.Transacao;
import com.br.ccs.mark.version.on.ccsmark.repository.ClienteRepository;
import com.br.ccs.mark.version.on.ccsmark.repository.ContaClienteRepository;
import com.br.ccs.mark.version.on.ccsmark.repository.TransacaoRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@EnableScheduling
public class CcsService {

    private final long MINUTOS = (5000 * 60);

    @Autowired
    private final ClienteRepository clienteRepository;

    @Autowired
    private final ContaClienteRepository contaClienteRepository;

    @Autowired
    private final TransacaoRepository transacaoRepository;

    private final CcsKafka kafkaProperties = new CcsKafka();

    public CcsService(ClienteRepository convidadoRepository, ContaClienteRepository contaClienteRepository, TransacaoRepository transacaoRepository) {
        this.clienteRepository = convidadoRepository;
        this.contaClienteRepository = contaClienteRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public Iterable<Cliente> obterTodosClientes() {
        return clienteRepository.findAll();
    }

    public Iterable<ContaCliente> obterTodasContas() {
        return contaClienteRepository.findAll();
    }

    public List<ContaCliente> pesquisarPorData(Date dataAtualizacao) {
        return contaClienteRepository.findByDataAtualizacao(dataAtualizacao);
    }

    public void escreverArquivo(List<ContaCliente> contaClienteList, Date dataAtualizacao) throws IOException {
        FileWriter arquivo = new FileWriter("relatorioTransacao" + new Date());

        BufferedWriter bufferedWriter = new BufferedWriter(arquivo);
        bufferedWriter.write("CPF;NOME;SALDO");
        for (ContaCliente contaCliente : contaClienteList) {
            bufferedWriter.flush();
            if (verificarSaldo(contaCliente)) {
                bufferedWriter.newLine();
                bufferedWriter.write(contaCliente.getIdCliente().getCpf() + ";");
                bufferedWriter.write(contaCliente.getIdCliente().getNome() + ";");
                bufferedWriter.write(String.valueOf(contaCliente.getSaldoConta()));
            }
        }
        bufferedWriter.close();
    }


    public void escreverArquivoTodo(List<ContaCliente> contaClienteList) throws IOException {
        FileWriter arquivo = new FileWriter("relatorioTransacao");

        BufferedWriter bufferedWriter = new BufferedWriter(arquivo);
        bufferedWriter.write("CPF;NOME;SALDO");
        for (ContaCliente contaCliente : contaClienteList) {
            bufferedWriter.newLine();
            bufferedWriter.write(contaCliente.getIdCliente().getNome() + ";");
        }

        bufferedWriter.close();
    }

    private boolean verificarSaldo(ContaCliente contaCliente) {
        return contaCliente.getSaldoConta() > 0;
    }

    public void saveTransaction(ContaCliente contaCliente) {
        contaClienteRepository.save(contaCliente);
    }

    public void saveCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }


    //    @Scheduled(fixedDelay = MINUTOS)
    public void enviarPeloKafka() {

        Date dataAtualizacao = new Date();

        // create the producer
        KafkaProducer<String, Cliente> producer = new KafkaProducer<>(kafkaProperties.configurationKafka());

        // create a producer record
        ProducerRecord<String, Cliente> record;

        List<Cliente> contaClienteList = clienteRepository.findByDataAtualizacao(dataAtualizacao);

        for (Cliente cliente : contaClienteList) {
            record = new ProducerRecord<>("ccs_mark", cliente);
            producer.send(record);
        }

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
    }

    public void enviarPeloKafkaAssincrono() {

        // create the producer
        KafkaProducer<String, Cliente> producer = new KafkaProducer<>(kafkaProperties.configurationKafka());

        // create a producer record
        ProducerRecord<String, Cliente> record;

        List<Cliente> contaClienteList = clienteRepository.findAll();

        for (Cliente cliente : contaClienteList) {
            record = new ProducerRecord<>("ccs_mark", cliente);
            producer.send(record);
        }

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
    }

    public void atualizarSaldo() {
        List<Long> contaClienteList = new ArrayList<>();

        for (ContaCliente contaCliente : contaClienteRepository.findAll()) {
            contaClienteList.add(contaCliente.getIdConta());
        }
        Random gerador = new Random();
        long id = gerador.nextInt(Math.toIntExact(contaClienteList.stream().collect(Collectors.summarizingLong(Long::longValue)).getMax()));
        if (contaClienteList.contains(id)) {
            DecimalFormat formatter = new DecimalFormat("##,###");
            Transacao transacao = new Transacao(id, Double.valueOf(formatter.format(gerador.nextDouble() *100)), LocalDate.now(), TipoTransacao.pegarTransacaoAleatoria());
            transacaoRepository.save(transacao);

            ContaCliente contaCliente = contaClienteRepository.findByIdConta(id);
            Double saldo = contaCliente.getSaldoConta() + transacao.getValorTransacao();
            contaCliente.setSaldoConta(saldo);
            contaClienteRepository.save(contaCliente);


            System.out.println(transacao.toString());
            System.out.println(contaCliente.toString());
        }
    }

    public List<Transacao> buscarTransacoes(Long idContaCliente) {
        return transacaoRepository.findByIdContaCliente(idContaCliente);
    }
}