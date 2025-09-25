package com.shortthirdman.bootlabs.eventsourcing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String itemId;
    private String itemName;
    private int quantity;
    private BigDecimal price;
}
