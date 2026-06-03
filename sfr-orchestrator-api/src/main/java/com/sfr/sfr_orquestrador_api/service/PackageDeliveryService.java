package com.sfr.sfr_orquestrador_api.service;

import java.util.UUID;

import com.sfr.sfr_orquestrador_api.controller.dto.PackageDeliveryRequest;

public interface PackageDeliveryService {

    public UUID create(PackageDeliveryRequest packageDelivery);
}
