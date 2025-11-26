package org.ecom.liftify.dto.response;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        LocalDateTime dateTime
) {}
