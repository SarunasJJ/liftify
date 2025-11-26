package org.ecom.liftify.dto.response.order;

import org.ecom.liftify.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderListItemResponse(
        Long id,
        String trackingNumber,
        BigDecimal orderValue,
        Order.OrderStatus status,
        LocalDateTime createdAt,
        Integer totalItems
) {
}
