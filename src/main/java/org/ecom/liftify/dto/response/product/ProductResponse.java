package org.ecom.liftify.dto.response.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        Integer remainingStock,
        Double averageRating,
        Integer totalRatings,
        List<String> imageUrls
) {}
