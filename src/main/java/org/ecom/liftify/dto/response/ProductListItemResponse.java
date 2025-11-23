package org.ecom.liftify.dto.response;

import java.math.BigDecimal;

public record ProductListItemResponse(
        Long id,
        String title,
        BigDecimal price,
        Integer remainingStock,
        Double averageRating,
        String primaryImageUrl
) {}
