package org.ecom.liftify.dto.request.order;

import jakarta.validation.constraints.NotNull;
import org.ecom.liftify.entity.Order;

public record UpdateOrderRequest (
        @NotNull(message = "Status is required")
        Order.OrderStatus status
){}
