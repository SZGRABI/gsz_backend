package cz.rohlik.gsz.repository;

import cz.rohlik.gsz.entity.Order;
import cz.rohlik.gsz.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByStatusAndExpiresAtBefore(OrderStatus status, LocalDateTime expiresAt);
}