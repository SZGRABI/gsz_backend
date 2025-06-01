package cz.rohlik.gsz.repository;

import cz.rohlik.gsz.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
