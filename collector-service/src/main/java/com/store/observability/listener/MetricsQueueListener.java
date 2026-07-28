package com.store.observability.listener;

import com.store.observability.config.RabbitMQConfig;
import com.store.observability.dto.MetricMessage;
import com.store.observability.service.MetricsBatchCollectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsQueueListener {

    private final MetricsBatchCollectorService collectorService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void onMessage(MetricMessage message) {
        log.info("Mensagem de métrica recebida da fila RabbitMQ: {}", message);
        collectorService.addMetric(message);
    }
}
