package com.shortthirdman.bootlabs.eventsourcing.service;

import com.shortthirdman.bootlabs.eventsourcing.domain.FoodOrder;
import com.shortthirdman.bootlabs.eventsourcing.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodOrderService {

    private final EventRepository eventRepository;

    public void cancelOrder(UUID orderId, String reason) {
    }

    public void deliverOrder(UUID orderId) {
    }

    public void acceptOrder(UUID orderId) {
    }

    public void pickUpOrder(UUID orderId, String driverId) {
    }

    public void markFoodReady(UUID orderId) {
    }

    public void startFoodPreparation(UUID orderId) {
    }

    public void addOrderItem(UUID orderId, String itemId, String itemName, int quantity, BigDecimal price) {
    }

    public FoodOrder getOrder(UUID orderId) {
        return null;
    }

    public UUID createOrder(String customerId, String restaurantId, String deliveryAddress, String deliveryNotes) {
        return null;
    }
}
