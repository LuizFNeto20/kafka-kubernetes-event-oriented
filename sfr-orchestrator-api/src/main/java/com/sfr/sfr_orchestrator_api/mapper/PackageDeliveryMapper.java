package com.sfr.sfr_orchestrator_api.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sfr.sfr_orchestrator_api.avro.RequestStartedEvent;
import com.sfr.sfr_orchestrator_api.domain.entity.PackageDelivery;
import com.sfr.sfr_orchestrator_api.domain.entity.PackageDimension;
import com.sfr.sfr_orchestrator_api.domain.entity.PackageRegion;
import com.sfr.sfr_orchestrator_api.domain.enums.DeliveryStatus;
import com.sfr.sfr_orchestrator_api.dto.PackageDeliveryRequest;

@Component
public class PackageDeliveryMapper {
        public static PackageDelivery toDelivery(PackageDeliveryRequest request, UUID orderId, UUID correlationId) {
                return PackageDelivery.builder()
                                .orderId(orderId)
                                .correlationId(correlationId)
                                .dimension(PackageDimension.builder()
                                                .height(request.height())
                                                .width(request.width())
                                                .length(request.length())
                                                .weight(request.weight())
                                                .build())
                                .region(PackageRegion.builder()
                                                .originZipCode(request.originZipCode())
                                                .destinationZipCode(request.destinationZipCode())
                                                .build())
                                .status(DeliveryStatus.STARTED)
                                .build();
        }

        public static RequestStartedEvent toEvent(PackageDelivery delivery) {
                return RequestStartedEvent.newBuilder()
                                .setOrderId(delivery.getOrderId())
                                .setCorrelationId(delivery.getCorrelationId())
                                .setHeight(delivery.getDimension().getHeight())
                                .setWidth(delivery.getDimension().getWidth())
                                .setLength(delivery.getDimension().getLength())
                                .setWeight(delivery.getDimension().getWeight())
                                .setOriginZipCode(delivery.getRegion().getOriginZipCode())
                                .setDestinationZipCode(delivery.getRegion().getDestinationZipCode())
                                .build();
        }
}
