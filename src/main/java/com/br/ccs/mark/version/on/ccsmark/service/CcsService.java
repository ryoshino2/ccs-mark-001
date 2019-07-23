package com.br.ccs.mark.version.on.ccsmark.service;

import com.br.ccs.mark.version.on.ccsmark.model.Cliente;
import com.br.ccs.mark.version.on.ccsmark.model.ClienteSerializer;
import com.br.ccs.mark.version.on.ccsmark.model.ContaCliente;
import com.br.ccs.mark.version.on.ccsmark.repository.ClienteRepository;
import com.br.ccs.mark.version.on.ccsmark.repository.ContaClienteRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
//@EnableScheduling
public class CcsService {

    private final long MINUTOS = (5000 * 60);

    @Autowired
    private final ClienteRepository clienteRepository;

    @Autowired
    private final ContaClienteRepository contaClienteRepository;

    public CcsService(ClienteRepository convidadoRepository, ContaClienteRepository contaClienteRepository) {
        this.clienteRepository = convidadoRepository;
        this.contaClienteRepository = contaClienteRepository;
    }

    public Iterable<Cliente> obterTodosClientes() {
        return clienteRepository.findAll();
    }

    public Iterable<ContaCliente> obterTodasContas() {
        return contaClienteRepository.findAll();
    }

    public List<ContaCliente> pesquisarPorData(Date dataAtualizacao) {
//        System.out.println(testeContador(dataAtualizacao));
        return contaClienteRepository.findByDataAtualizacao(dataAtualizacao);
    }

    private long testeContador(Date dataAtualizacao) {
        return contaClienteRepository.countByDataAtualizacao(dataAtualizacao);
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

    public void saveCliente(Cliente cliente){
        clienteRepository.save(cliente);
    }


//    @Scheduled(fixedDelay = MINUTOS)
    public void enviarPeloKafka() {

        Date dataAtualizacao = new Date();

        String bootstrapServers = "127.0.0.1:9092";
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ClienteSerializer.class.getName());

        // create the producer
        KafkaProducer<String, Cliente> producer = new KafkaProducer<>(properties);

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

        String bootstrapServers = "127.0.0.1:9092";
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ClienteSerializer.class.getName());

        // create the producer
        KafkaProducer<String, Cliente> producer = new KafkaProducer<>(properties);

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
}