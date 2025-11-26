package org.ecom.liftify.repository;

import org.ecom.liftify.entity.Order;
import org.ecom.liftify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findByCustomer(User user);

    Order findOrderByTrackingNumber(String trackingNumber);

    Boolean existsByTrackingNumber(String trackingNumber);

    List<Order> findOrderByStatus(Order.OrderStatus status);

}
