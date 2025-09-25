package com.shortthirdman.bootlabs.eventsourcing.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class AnalyticsConsumer {

    private final Map<String, AtomicInteger> restaurantOrderCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> menuItemPopularity = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> orderCancellationReasons = new ConcurrentHashMap<>();

    @KafkaListener(topics = "order-events", groupId = "analytics-service")
    public void consume(Object eventObject) {
        if (eventObject instanceof OrderCreatedEvent) {
            OrderCreatedEvent event = (OrderCreatedEvent) eventObject;
            String restaurantId = event.getRestaurantId();

            // Update restaurant order count
            restaurantOrderCounts.computeIfAbsent(restaurantId, k -> new AtomicInteger(0))
                    .incrementAndGet();

            log.info("Analytics: New order for restaurant {}. Total orders: {}",
                    restaurantId,
                    restaurantOrderCounts.get(restaurantId).get());
        } else if (eventObject instanceof OrderItemAddedEvent) {
            OrderItemAddedEvent event = (OrderItemAddedEvent) eventObject;
            String itemId = event.getItemId();

            // Update menu item popularity
            menuItemPopularity.computeIfAbsent(itemId, k -> new AtomicInteger(0))
                    .addAndGet(event.getQuantity());

            log.info("Analytics: Menu item {} ordered. Total ordered: {}",
                    itemId,
                    menuItemPopularity.get(itemId).get());
        } else if (eventObject instanceof OrderCancelledEvent) {
            OrderCancelledEvent event = (OrderCancelledEvent) eventObject;
            String reason = event.getReason() != null ? event.getReason() : "Unknown";

            // Track cancellation reasons
            orderCancellationReasons.computeIfAbsent(reason, k -> new AtomicInteger(0))
                    .incrementAndGet();

            log.info("Analytics: Order cancelled. Reason: {}. Frequency: {}",
                    reason,
                    orderCancellationReasons.get(reason).get());
        }
    }

    // Methods to expose analytics data
    public Map<String, Integer> getRestaurantOrderCounts() {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        restaurantOrderCounts.forEach((key, value) -> result.put(key, value.get()));
        return result;
    }

    public Map<String, Integer> getMenuItemPopularity() {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        menuItemPopularity.forEach((key, value) -> result.put(key, value.get()));
        return result;
    }

    public Map<String, Integer> getOrderCancellationReasons() {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        orderCancellationReasons.forEach((key, value) -> result.put(key, value.get()));
        return result;
    }
}
