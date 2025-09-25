package com.shortthirdman.bootlabs.eventsourcing.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;

@Component
public class EventFactory {

    public OrderCreatedEvent createOrderCreatedEvent(UUID orderId, String customerId, String restaurantId,
                                                     String deliveryAddress, String deliveryNotes) {
        BaseEvent baseEvent = createBaseEvent(orderId, OrderCreatedEvent.class.getSimpleName());

        return OrderCreatedEvent.newBuilder()
                .setBase(baseEvent)
                .setCustomerId(customerId).setRestaurantId(restaurantId)
                .setDeliveryAddress(deliveryAddress)
                .setDeliveryNotes(deliveryNotes)
                .build();
    }

    public OrderItemAddedEvent createOrderItemAddedEvent(UUID orderId, String itemId, String itemName,
                                                         int quantity, BigDecimal price) {
        BaseEvent baseEvent = createBaseEvent(orderId, OrderItemAddedEvent.class.getSimpleName());

        // Convert BigDecimal to Avro's decimal representation (ByteBuffer)
        ByteBuffer priceBytes = ByteBuffer.wrap(price.unscaledValue().toByteArray());

        return OrderItemAddedEvent.newBuilder()
                .setBase(baseEvent)
                .setItemId(itemId)
                .setItemName(itemName)
                .setQuantity(quantity)
                .setPrice(priceBytes)
                .build();
    }

    public OrderAcceptedEvent createOrderAcceptedEvent(UUID orderId) {
        BaseEvent baseEvent = createBaseEvent(orderId, OrderAcceptedEvent.class.getSimpleName());

        return OrderAcceptedEvent.newBuilder()
                .setBase(baseEvent)
                .build();
    }

    public FoodPreparationStartedEvent createFoodPreparationStartedEvent(UUID orderId) {
        BaseEvent baseEvent = createBaseEvent(orderId, FoodPreparationStartedEvent.class.getSimpleName());

        return FoodPreparationStartedEvent.newBuilder()
                .setBase(baseEvent)
                .build();
    }

    public FoodReadyEvent createFoodReadyEvent(UUID orderId) {
        BaseEvent baseEvent = createBaseEvent(orderId, FoodReadyEvent.class.getSimpleName());

        return FoodReadyEvent.newBuilder()
                .setBase(baseEvent)
                .build();
    }

    public OrderPickedUpEvent createOrderPickedUpEvent(UUID orderId, String driverId) {
        BaseEvent baseEvent = createBaseEvent(orderId, OrderPickedUpEvent.class.getSimpleName());

        return OrderPickedUpEvent.newBuilder()
                .setBase(baseEvent)
                .setDriverId(driverId)
                .build();
    }

    public OrderDeliveredEvent createOrderDeliveredEvent(UUID orderId) {
        BaseEvent baseEvent = createBaseEvent(orderId, OrderDeliveredEvent.class.getSimpleName());
        Instant now = Instant.now();

        return OrderDeliveredEvent.newBuilder()
                .setBase(baseEvent)
                .setDeliveryTime(now)
                .build();
    }

    public OrderCancelledEvent createOrderCancelledEvent(UUID orderId, String reason) {
        BaseEvent baseEvent = createBaseEvent(orderId, OrderCancelledEvent.class.getSimpleName());

        return OrderCancelledEvent.newBuilder()
                .setBase(baseEvent)
                .setReason(reason)
                .build();
    }

    private BaseEvent createBaseEvent(UUID aggregateId, String eventType) {
        return BaseEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setAggregateId(aggregateId.toString())
                .setTimestamp(Instant.now())
                .setEventType(eventType)
                .build();
    }
}
