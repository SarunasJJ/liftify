package org.ecom.liftify.service;

import org.ecom.liftify.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TrackingNumberService {
    private final OrderRepository orderRepository;
    private final SecureRandom random = new SecureRandom();

    public TrackingNumberService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String generateUniqueTrackingNumber() {
        String trackingNumber;
        int maxAttempts = 10;
        int attempts = 0;

        do {
            trackingNumber = generateTrackingNumber();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new RuntimeException("Failed to generate unique tracking number");
            }
        } while (orderRepository.existsByTrackingNumber(trackingNumber));

        return trackingNumber;
    }

    private String generateTrackingNumber() {

        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String randomPart = generateRandomAlphanumeric();

        return String.format("ORD-%s-%s", date, randomPart);
    }

    private String generateRandomAlphanumeric() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Removed confusing chars: 0, O, I, 1
        StringBuilder sb = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}

