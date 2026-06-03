package com.sfr.sfr_orchestrator_api.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.sfr.sfr_orchestrator_api.avro.RequestStartedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PackageDeliveryProducer {

    @Value("${kafka.topic.package-delivery}")
    private String topic;

    private final KafkaTemplate<String, RequestStartedEvent> kafkaTemplate;

    public void send(RequestStartedEvent event) {
        kafkaTemplate.send(
                topic,
                event.getOrderId().toString(),
                event).whenComplete((result, ex) -> {
                    if (ex != null) {
                        handleFailure(event.getOrderId().toString(), event, ex);
                    } else {
                        handleSuccess(event.getOrderId().toString(), result);
                    }
                });
    }

    private void handleSuccess(String key, SendResult<String, RequestStartedEvent> result) {
        log.info("Evento Avro publicado com sucesso! Pedido: {}, Particao: {}, Offset: {}",
                key,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }

    private void handleFailure(String key, RequestStartedEvent event, Throwable ex) {
        log.error("Falha critica ao enviar evento Avro para o pedido: {}. Objeto: {}. Erro: {}",
                key, event, ex.getMessage(), ex);
    }
}
