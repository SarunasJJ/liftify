package org.ecom.liftify.dto.response.order;

public record CustomerInfo(
        Long id,
        String email,
        String fullName
) {
}
