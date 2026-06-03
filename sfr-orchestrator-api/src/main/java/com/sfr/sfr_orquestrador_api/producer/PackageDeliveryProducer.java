package com.sfr.sfr_orquestrador_api.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.sfr.sfr_orquestrador_api.avro.RequestStartedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PackageDeliveryProducer {

    @Value("${kafka.topic.package-delivery}")
    private String topic;

    private final KafkaTemplate<String, RequestStartedEvent> kafkaTemplate;

    public void send(RequestStartedEvent event) {
        kafkaTemplate.send(
            topic, 
            event.getOrderId().toString(),
            event
        );
    }
}
