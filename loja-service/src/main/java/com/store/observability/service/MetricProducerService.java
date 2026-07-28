package com.store.observability.service;

import com.store.observability.config.RabbitMQConfig;
import com.store.observability.dto.MetricMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricProducerService {

    private final RabbitTemplate rabbitTemplate;

    public void sendMetric(String action, Double value) {
        MetricMessage message = MetricMessage.builder()
                .service("loja-frontend")
                .action(action)
                .timestamp(System.currentTimeMillis())
                .value(value != null ? value : 0.0)
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
        log.info("Métrica enviada para RabbitMQ: {}", message);
    }
}
