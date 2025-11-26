package org.ecom.liftify.dto.request.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @Min(value = 0, message = "Stock cannot be negative")
        Integer remainingStock
) {
    public CreateProductRequest {
        if (remainingStock == null) {
            remainingStock = 0;
        }
    }
}
