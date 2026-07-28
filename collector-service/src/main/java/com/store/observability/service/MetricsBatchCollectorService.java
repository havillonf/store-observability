package com.store.observability.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.store.observability.dto.MetricMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsBatchCollectorService {

    private final ElasticsearchClient esClient;

    private final ConcurrentLinkedQueue<MetricMessage> buffer = new ConcurrentLinkedQueue<>();

    @Value("${collector.batch.size:100}")
    private int batchSize;

    @Value("${elasticsearch.index:metrics-poc}")
    private String indexName;

    public void addMetric(MetricMessage metric) {
        buffer.add(metric);
        log.info("Métrica adicionada ao buffer. Tamanho atual do buffer: {}", buffer.size());
        if (buffer.size() >= batchSize) {
            log.info("Tamanho limite de lote ({}) atingido. Disparando flush...", batchSize);
            flush();
        }
    }

    @Scheduled(fixedRateString = "${collector.batch.flush-interval-ms:5000}")
    public void scheduledFlush() {
        if (!buffer.isEmpty()) {
            log.info("Tempo limite atingido. Disparando flush agendado do buffer...");
            flush();
        }
    }

    public synchronized void flush() {
        if (buffer.isEmpty()) {
            return;
        }

        List<MetricMessage> batch = new ArrayList<>();
        MetricMessage item;
        while ((item = buffer.poll()) != null) {
            batch.add(item);
        }

        if (batch.isEmpty()) {
            return;
        }

        log.info("Processando lote de {} métricas para envio ao Elasticsearch (índice: {})...", batch.size(), indexName);

        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
            for (MetricMessage metric : batch) {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(indexName)
                                .document(metric)
                        )
                );
            }

            BulkResponse response = esClient.bulk(br.build());
            if (response.errors()) {
                log.error("Ocorreram erros durante a operação de Bulk no Elasticsearch");
                response.items().stream()
                        .filter(i -> i.error() != null)
                        .forEach(i -> log.error("Erro na ingestão do item: {}", i.error().reason()));
            } else {
                log.info("Lote de {} métricas indexado com sucesso no Elasticsearch em {} ms!", batch.size(), response.took());
            }
        } catch (Exception e) {
            log.error("Falha ao enviar lote de métricas para o Elasticsearch", e);
        }
    }
}
