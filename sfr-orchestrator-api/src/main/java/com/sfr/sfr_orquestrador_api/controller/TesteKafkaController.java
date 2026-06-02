package com.sfr.sfr_orquestrador_api.controller;

import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sfr.sfr_orquestrador_api.avro.RequestStartedEvent;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teste")
@RequiredArgsConstructor
public class TesteKafkaController {

    private final KafkaTemplate<String, RequestStartedEvent> kafkaTemplate;

    @PostMapping
    public String enviar() {

        UUID orderId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        RequestStartedEvent evento =
                RequestStartedEvent.newBuilder()
                        .setOrderId(orderId)
                        .setCorrelationId(correlationId)
                        .setWeight(10.0)
                        .setHeight(20.0)
                        .setWidth(30.0)
                        .setLength(40.0)
                        .setOriginZipCode("79000000")
                        .setDestinationZipCode("01001000")
                        .setCargoType("NORMAL")
                        .build();

        kafkaTemplate.send(
                "frete.comando.gerar.tipo_entrega",
                orderId.toString(),
                evento
        );

        return "ok";
    }
}