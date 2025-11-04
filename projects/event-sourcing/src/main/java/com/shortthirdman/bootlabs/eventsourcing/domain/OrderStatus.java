package com.shortthirdman.bootlabs.eventsourcing.domain;

public enum OrderStatus {
    CREATED,
    ACCEPTED,
    PREPARING,
    READY,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}
