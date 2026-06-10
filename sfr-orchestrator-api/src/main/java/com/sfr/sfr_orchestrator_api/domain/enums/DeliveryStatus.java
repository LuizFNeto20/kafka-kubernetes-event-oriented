package com.sfr.sfr_orchestrator_api.domain.enums;

public enum DeliveryStatus {
    STARTED(1),
    PENDING_DELIVERY_TYPE(2),
    DELIVERY_TYPE_DEFINED(3),
    FINISHED(4);

    private int code;

    private DeliveryStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static DeliveryStatus valueOf(int code) {
        for (DeliveryStatus value : DeliveryStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid code");
    }
}