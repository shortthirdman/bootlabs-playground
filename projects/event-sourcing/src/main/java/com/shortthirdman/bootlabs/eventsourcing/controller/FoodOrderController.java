package com.shortthirdman.bootlabs.eventsourcing.controller;

import com.shortthirdman.bootlabs.eventsourcing.domain.FoodOrder;
import com.shortthirdman.bootlabs.eventsourcing.listener.AnalyticsConsumer;
import com.shortthirdman.bootlabs.eventsourcing.repository.AnalyticsRepository;
import com.shortthirdman.bootlabs.eventsourcing.service.FoodOrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class FoodOrderController {

    private final FoodOrderService foodOrderService;
    private final AnalyticsConsumer analyticsConsumer;
    private final AnalyticsRepository analyticsRepository;

    @PostMapping
    public ResponseEntity<UUID> createOrder(@RequestBody CreateOrderRequest request) {
        UUID orderId = foodOrderService.createOrder(
                request.getCustomerId(),
                request.getRestaurantId(),
                request.getDeliveryAddress(),
                request.getDeliveryNotes()
        );
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<FoodOrder> getOrder(@PathVariable UUID orderId) {
        FoodOrder order = foodOrderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Void> addOrderItem(@PathVariable UUID orderId, @RequestBody AddItemRequest request) {
        foodOrderService.addOrderItem(
                orderId,
                request.getItemId(),
                request.getItemName(),
                request.getQuantity(),
                request.getPrice()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/accept")
    public ResponseEntity<Void> acceptOrder(@PathVariable UUID orderId) {
        foodOrderService.acceptOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/prepare")
    public ResponseEntity<Void> startFoodPreparation(@PathVariable UUID orderId) {
        foodOrderService.startFoodPreparation(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/ready")
    public ResponseEntity<Void> markFoodReady(@PathVariable UUID orderId) {
        foodOrderService.markFoodReady(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/pickup")
    public ResponseEntity<Void> pickUpOrder(@PathVariable UUID orderId, @RequestBody PickupRequest request) {
        foodOrderService.pickUpOrder(orderId, request.getDriverId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<Void> deliverOrder(@PathVariable UUID orderId) {
        foodOrderService.deliverOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID orderId, @RequestBody CancelRequest request) {
        foodOrderService.cancelOrder(orderId, request.getReason());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/analytics/restaurant-orders")
    public ResponseEntity<Map<String, Integer>> getRestaurantOrderCounts() {
        return ResponseEntity.ok(analyticsConsumer.getRestaurantOrderCounts());
    }

    @GetMapping("/analytics/popular-items")
    public ResponseEntity<Map<String, Integer>> getPopularItems() {
        return ResponseEntity.ok(analyticsConsumer.getMenuItemPopularity());
    }

    @GetMapping("/analytics/cancellation-reasons")
    public ResponseEntity<Map<String, Integer>> getCancellationReasons() {
        return ResponseEntity.ok(analyticsConsumer.getOrderCancellationReasons());
    }

    @GetMapping("/analytics/event-counts")
    public ResponseEntity<Map<String, Long>> getEventCounts() {
        return ResponseEntity.ok(analyticsRepository.getEventCountByType());
    }

    @GetMapping("/analytics/daily-metrics")
    public ResponseEntity<List<AnalyticsRepository.DailyOrderMetrics>> getDailyMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsRepository.getDailyOrderMetrics(startDate, endDate));
    }

    @Data
    public static class CreateOrderRequest {
        private String customerId;
        private String restaurantId;
        private String deliveryAddress;
        private String deliveryNotes;
    }

    @Data
    public static class AddItemRequest {
        private String itemId;
        private String itemName;
        private int quantity;
        private BigDecimal price;
    }

    @Data
    public static class PickupRequest {
        private String driverId;
    }

    @Data
    public static class CancelRequest {
        private String reason;
    }
}
