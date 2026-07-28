package com.store.observability.service;

import com.store.observability.config.RabbitMQConfig;
import com.store.observability.dto.MetricMessage;
import com.store.observability.entity.UserInteraction;
import com.store.observability.repository.UserInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final UserInteractionRepository interactionRepository;

    public void sendMetric(String action, Double value) {
        long timestamp = System.currentTimeMillis();
        double val = value != null ? value : 0.0;

        // Persist interaction into PostgreSQL
        UserInteraction interaction = UserInteraction.builder()
                .service("loja-frontend")
                .action(action)
                .timestamp(timestamp)
                .value(val)
                .createdAt(LocalDateTime.now())
                .build();
        interactionRepository.save(interaction);
        log.info("Interação salva no banco PostgreSQL: {}", interaction);

        // Publish metric event to RabbitMQ
        MetricMessage message = MetricMessage.builder()
                .service("loja-frontend")
                .action(action)
                .timestamp(timestamp)
                .value(val)
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
        log.info("Métrica enviada para RabbitMQ: {}", message);
    }
}
