package com.sfr.sfr_orquestrador_api.controller.dto;

import java.util.UUID;

import com.sfr.sfr_orquestrador_api.domain.entity.PackageDimension;
import com.sfr.sfr_orquestrador_api.domain.entity.PackageRegion;
import com.sfr.sfr_orquestrador_api.domain.enums.DeliveryStatus;
import com.sfr.sfr_orquestrador_api.domain.enums.DeliveryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PackageDeliveryRequest(
    @NotNull
    double height,
    @NotNull
    double width,
    @NotNull
    double length,
    @NotNull
    double weight,
    @NotNull
    String originZipCode,
    @NotNull
    String destinationZipCode,
    @NotNull
    UUID correlationId,    
    @NotBlank
    DeliveryStatus status,
    @NotBlank
    DeliveryType deliveryType
) {}
