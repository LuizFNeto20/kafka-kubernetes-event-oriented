package com.sfr.sfr_orchestrator_api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sfr.sfr_orchestrator_api.dto.PackageDeliveryRequest;
import com.sfr.sfr_orchestrator_api.mapper.PackageDeliveryMapper;
import com.sfr.sfr_orchestrator_api.producer.PackageDeliveryProducer;
import com.sfr.sfr_orchestrator_api.repository.PackageDeliveryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PackageDeliveryService {

    private final PackageDeliveryRepository repository;
    private final PackageDeliveryProducer producer;

    public UUID create(PackageDeliveryRequest packageDelivery) {

        UUID orderId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        producer.send(PackageDeliveryMapper
                .toEvent(repository.save(PackageDeliveryMapper.toDelivery(packageDelivery, orderId, correlationId))));

        return orderId;
    }
}
