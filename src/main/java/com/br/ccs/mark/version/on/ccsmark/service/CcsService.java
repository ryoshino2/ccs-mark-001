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
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@EnableScheduling
public class CcsService {

    //288 transacoes por dia
    private final long GERAR_TRANSACAO = (5000 * 60);
    private final CcsKafka kafkaProperties = new CcsKafka();

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ContaClienteRepository contaClienteRepository;
    @Autowired
    private TransacaoRepository transacaoRepository;


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


    public void enviarPeloKafka() {
        // create the producer
        KafkaProducer<String, Cliente> producer = new KafkaProducer<>(kafkaProperties.configurationKafka());

        // create a producer record
        ProducerRecord<String, Cliente> record;

        List<Cliente> contaClienteList = clienteRepository.findByDataAtualizacao(new Date());

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

        List<Cliente> contaClienteList = clienteRepository.findByDataAtualizacao(new Date());

        for (Cliente cliente : contaClienteList) {
            record = new ProducerRecord<>("ccs_mark", cliente);
            producer.send(record);
        }
        System.out.println(contaClienteList.get(0).getEmail());
        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
    }

    @Scheduled(fixedDelay = GERAR_TRANSACAO)
    public void atualizarSaldo() {
        Double saldo = null;
        Transacao transacao = gerarTransacao();
        ContaCliente contaCliente = contaClienteRepository.findByIdConta(transacao.getIdContaCliente());
        try {
            saldo = efetuarTransacao(transacao, contaCliente, saldo);
            atualizarSaldoContaCliente(getIdContaCliente(), transacao, saldo);
            atualizarDataDeMovimentacaoCliente(contaCliente.getIdCliente().getIdCliente(), transacao);
        } catch (NullPointerException e) {
            System.out.println("Cliente com id: " + transacao.getIdContaCliente() + " não localizado");
        }
    }


    private long getIdContaCliente() {
        List<Long> contaClienteList = buscarIdsContaClientes();
        Random gerador = new Random();
        return gerador.nextInt(Math.toIntExact(contaClienteList.stream().collect(Collectors.summarizingLong(Long::longValue)).getMax())) + 1;
    }

    private Double efetuarTransacao(Transacao transacao, ContaCliente contaCliente, Double saldo) {
        if (transacao.getTipoTransacao() == TipoTransacao.CREDIT) {
            saldo = contaCliente.getSaldoConta() + transacao.getValorTransacao();
        } else if (transacao.getTipoTransacao() == TipoTransacao.DEBIT) {
            saldo = contaCliente.getSaldoConta() - transacao.getValorTransacao();
        }
        return saldo;
    }

    private List<Long> buscarIdsContaClientes() {
        List<Long> contaClienteList = new ArrayList<>();

        for (ContaCliente contaCliente : contaClienteRepository.findAll()) {
            contaClienteList.add(contaCliente.getIdConta());
        }
        return contaClienteList;
    }

    public Transacao gerarTransacao() {
        long id = getIdContaCliente();
        Transacao transacao = null;
        try {
            DecimalFormat formatter = new DecimalFormat("##,###");
            Random gerador = new Random();
            transacao = new Transacao(id, Double.valueOf(formatter.format(gerador.nextDouble() * 100)), new Date(), TipoTransacao.pegarTransacaoAleatoria());
            salvarTransacao(transacao);
        } catch (NullPointerException e) {
            System.out.println("nao funcionou");
        }
        return transacao;
    }

    private void salvarTransacao(Transacao transacao) {
        transacaoRepository.save(transacao);
    }

    public void atualizarDataDeMovimentacaoCliente(Long id, Transacao transacao) {
        Cliente cliente = clienteRepository.findByIdCliente(id);
        cliente.setDataAtualizacao(transacao.getDataTransacao());
        clienteRepository.save(cliente);
    }

    public void atualizarSaldoContaCliente(Long id, Transacao transacao, Double saldo) {
        ContaCliente contaCliente = contaClienteRepository.findByIdConta(id);
        contaCliente.setDataAtualizacao(transacao.getDataTransacao());
        contaCliente.setSaldoConta(saldo);
        contaClienteRepository.save(contaCliente);
    }

    public List<Transacao> buscarTransacoes(Long idContaCliente) {
        return transacaoRepository.findByIdContaCliente(idContaCliente);
    }
}