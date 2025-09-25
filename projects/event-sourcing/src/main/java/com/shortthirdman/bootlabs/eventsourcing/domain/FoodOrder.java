package com.shortthirdman.bootlabs.eventsourcing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodOrder {

    private UUID id;
    private String customerId;
    private String restaurantId;
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.CREATED;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private String deliveryAddress;
    private String deliveryNotes;

    public FoodOrder(UUID id, String customerId, String restaurantId, String deliveryAddress) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
    }

    public void addItem(String itemId, String itemName, int quantity, BigDecimal price) {
        OrderItem item = new OrderItem(itemId, itemName, quantity, price);
        items.add(item);
        recalculateTotal();
    }

    public void acceptOrder() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot accept order that is not in CREATED state");
        }
        status = OrderStatus.ACCEPTED;
    }

    public void startFoodPreparation() {
        if (status != OrderStatus.ACCEPTED) {
            throw new IllegalStateException("Cannot start preparation for order that is not in ACCEPTED state");
        }
        status = OrderStatus.PREPARING;
    }

    public void foodReady() {
        if (status != OrderStatus.PREPARING) {
            throw new IllegalStateException("Cannot mark food ready for order that is not in PREPARING state");
        }
        status = OrderStatus.READY;
    }

    public void pickedUpByDriver() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Cannot mark as picked up for order that is not in READY state");
        }
        status = OrderStatus.IN_DELIVERY;
    }

    public void delivered() {
        if (status != OrderStatus.IN_DELIVERY) {
            throw new IllegalStateException("Cannot mark as delivered for order that is not in IN_DELIVERY state");
        }
        status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel an order that is already delivered or cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
