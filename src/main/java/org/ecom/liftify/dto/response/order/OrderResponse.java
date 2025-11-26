package org.ecom.liftify.dto.response.order;

import org.ecom.liftify.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String trackingNumber,
        BigDecimal orderValue,
        Order.OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CustomerInfo customer,
        Integer totalItems,
        List<OrderItemResponse> orderItems
) {
}
