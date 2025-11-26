package org.ecom.liftify.dto.response.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productTitle,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
){
}
