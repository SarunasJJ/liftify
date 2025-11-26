package org.ecom.liftify.service;

import jakarta.transaction.Transactional;
import org.ecom.liftify.dto.request.order.CreateOrderRequest;
import org.ecom.liftify.dto.request.order.OrderItemRequest;
import org.ecom.liftify.dto.request.order.UpdateOrderRequest;
import org.ecom.liftify.dto.response.order.CustomerInfo;
import org.ecom.liftify.dto.response.order.OrderItemResponse;
import org.ecom.liftify.dto.response.order.OrderListItemResponse;
import org.ecom.liftify.dto.response.order.OrderResponse;
import org.ecom.liftify.entity.Order;
import org.ecom.liftify.entity.OrderItem;
import org.ecom.liftify.entity.Product;
import org.ecom.liftify.entity.User;
import org.ecom.liftify.exception.InsufficientStockException;
import org.ecom.liftify.exception.ResourceNotFoundException;
import org.ecom.liftify.exception.UnauthorizedException;
import org.ecom.liftify.repository.OrderRepository;
import org.ecom.liftify.repository.ProductRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuthenticationService authenticationService;
    private final TrackingNumberService  trackingNumberService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,AuthenticationService authenticationService, TrackingNumberService trackingNumberService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.authenticationService = authenticationService;
        this.trackingNumberService = trackingNumberService;
    }

    public OrderResponse createOrder(CreateOrderRequest request){
        User user = authenticationService.getCurrentUser();
        if(!authenticationService.isAuthenticated()){
            throw new UnauthorizedException("Unauthorized");
        }

        String trackingNumber = trackingNumberService.generateUniqueTrackingNumber();

        Order order = Order.builder()
                .trackingNumber(trackingNumber)
                .customer(user)
                .status(Order.OrderStatus.PENDING)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for(OrderItemRequest orderItemRequest : request.items()){
            Product product = productRepository.findById(orderItemRequest.productId())
                    .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

            if(product.getRemainingStock() < orderItemRequest.quantity()) {
                throw new InsufficientStockException("Insufficient stock");
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.quantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(orderItemRequest.quantity())
                    .price(subtotal)
                    .build();

            order.getOrderItems().add(orderItem);

            product.setRemainingStock(product.getRemainingStock() - orderItemRequest.quantity());

            total = total.add(subtotal);
        }
        order.setOrderValue(total);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    public OrderResponse updateOrderStatus(Long id, UpdateOrderRequest request){
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        validateStatusTransition(order.getStatus(), request.status());

        order.setStatus(request.status());

        return mapToResponse(order);
    }

    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {

        boolean isValid = switch (currentStatus) {
            case PENDING -> newStatus == Order.OrderStatus.PAID ||
                    newStatus == Order.OrderStatus.CANCELLED;
            case PAID -> newStatus == Order.OrderStatus.SHIPPED;
            case SHIPPED -> newStatus == Order.OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false; // Final states
        };

        if (!isValid) {
            throw new IllegalStateException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    private OrderResponse mapToResponse(Order order){
        CustomerInfo customerInfo = new CustomerInfo(
                order.getCustomer().getId(),
                order.getCustomer().getEmail(),
                order.getCustomer().getFullName()
        );

        List<OrderItemResponse> orderItems = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getTitle(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getTrackingNumber(),
                order.getOrderValue(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                customerInfo,
                order.getOrderItems().size(),
                orderItems
        );
    }

    private OrderListItemResponse mapToListItem(Order order) {
        return new OrderListItemResponse(
                order.getId(),
                order.getTrackingNumber(),
                order.getOrderValue(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getOrderItems().size()
        );
    }

}
