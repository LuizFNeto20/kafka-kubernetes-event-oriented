package com.sfr.sfr_orquestrador_api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sfr.sfr_orquestrador_api.avro.RequestStartedEvent;
import com.sfr.sfr_orquestrador_api.controller.dto.PackageDeliveryRequest;
import com.sfr.sfr_orquestrador_api.domain.entity.PackageDelivery;
import com.sfr.sfr_orquestrador_api.domain.entity.PackageDimension;
import com.sfr.sfr_orquestrador_api.domain.entity.PackageRegion;
import com.sfr.sfr_orquestrador_api.domain.enums.DeliveryStatus;
import com.sfr.sfr_orquestrador_api.producer.PackageDeliveryProducer;
import com.sfr.sfr_orquestrador_api.repository.PackageDeliveryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PackageDeliveryServiceImplements implements PackageDeliveryService {
    
    private final PackageDeliveryRepository repository;
    private final PackageDeliveryProducer producer;

    @Override
    public UUID create(PackageDeliveryRequest packageDelivery) {

        UUID orderId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        PackageDimension dimension = PackageDimension.builder()
            .height(packageDelivery.height())
            .width(packageDelivery.width())
            .length(packageDelivery.length())
            .weight(packageDelivery.weight())
            .build();

        PackageRegion region = PackageRegion.builder()
            .originZipCode(packageDelivery.originZipCode())
            .destinationZipCode(packageDelivery.destinationZipCode())
            .build();

        PackageDelivery delivery = PackageDelivery.builder()
            .orderId(orderId)
            .correlationId(correlationId)
            .dimension(dimension)
            .region(region)
            .status(DeliveryStatus.STARTED)
            .deliveryType(packageDelivery.deliveryType())
            .build();

        repository.save(delivery);

        RequestStartedEvent event = RequestStartedEvent.newBuilder()
            .setOrderId(orderId)
            .setCorrelationId(correlationId)
            .setHeight(packageDelivery.height())
            .setWidth(packageDelivery.width())
            .setLength(packageDelivery.length())
            .setWeight(packageDelivery.weight())
            .setOriginZipCode(packageDelivery.originZipCode())
            .setDestinationZipCode(packageDelivery.destinationZipCode())
            .setCargoType(packageDelivery.deliveryType().name())
            .build();

        producer.send(event);

        return orderId;
    }
}
