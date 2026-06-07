package com.sfr.sfr_orchestrator_api.infrastructure.persistence;

import com.sfr.sfr_orchestrator_api.application.port.JpaRepositoryPort;
import com.sfr.sfr_orchestrator_api.domain.entity.PackageDelivery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PackageDeliveryPersistenceAdapter implements JpaRepositoryPort {

    private final JpaPackageDeliveryRepository repository;

    @Override
    public PackageDelivery save(PackageDelivery delivery) {
        return repository.save(delivery);
    }

}
