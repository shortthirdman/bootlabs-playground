package com.shortthirdman.bootlabs.eventsourcing.repository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AnalyticsRepository {

    private final DSLContext dsl;

    public Map<String, Long> getEventCountByType() {
        EventStore e = EventStore.EVENT_STORE;

        Result<Record2<String, Integer>> result = dsl
                .select(e.EVENT_TYPE, dsl.count())
                .from(e)
                .groupBy(e.EVENT_TYPE)
                .fetch();

        return result.stream()
                .collect(Collectors.toMap(
                        r -> r.get(e.EVENT_TYPE),
                        r -> r.get(dsl.count()).longValue()
                ));
    }

    public List<DailyOrderMetrics> getDailyOrderMetrics(LocalDate startDate, LocalDate endDate) {
        EventStore e = EventStore.EVENT_STORE;

        // Convert LocalDate to Instant
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Result<Record4<LocalDate, Integer, Integer, Integer>> result = dsl
                .select(
                        dsl.cast(dsl.localDate(e.TIMESTAMP), LocalDate.class).as("day"),
                        dsl.countDistinct(e.AGGREGATE_ID).as("total_orders"),
                        dsl.countDistinct(
                                dsl.when(e.EVENT_TYPE.eq("OrderDeliveredEvent"), e.AGGREGATE_ID)
                        ).as("delivered_orders"),
                        dsl.countDistinct(
                                dsl.when(e.EVENT_TYPE.eq("OrderCancelledEvent"), e.AGGREGATE_ID)
                        ).as("cancelled_orders")
                )
                .from(e)
                .where(e.TIMESTAMP.between(startInstant, endInstant))
                .and(dsl.or(
                        e.EVENT_TYPE.eq("OrderCreatedEvent"),
                        e.EVENT_TYPE.eq("OrderDeliveredEvent"),
                        e.EVENT_TYPE.eq("OrderCancelledEvent")
                ))
                .groupBy(dsl.localDate(e.TIMESTAMP))
                .orderBy(dsl.localDate(e.TIMESTAMP))
                .fetch();

        return result.stream()
                .map(r -> new DailyOrderMetrics(
                        r.get("day", LocalDate.class),
                        r.get("total_orders", Integer.class),
                        r.get("delivered_orders", Integer.class),
                        r.get("cancelled_orders", Integer.class)
                ))
                .collect(Collectors.toList());
    }

    public List<RestaurantPerformance> getRestaurantPerformance() {
        // This would require more complex query joining with a restaurants table
        // and parsing event data to extract restaurant IDs and delivery times
        // For now, we'll return a placeholder
        return List.of();
    }

    @Data
    public static class RestaurantPerformance {
        private final String restaurantId;
        private final String restaurantName;
        private final int totalOrders;
        private final BigDecimal averageDeliveryTimeMinutes;
        private final BigDecimal cancellationRatePercent;
    }

    @Data
    public static class DailyOrderMetrics {
        private final LocalDate date;
        private final int totalOrders;
        private final int deliveredOrders;
        private final int cancelledOrders;
    }
}
