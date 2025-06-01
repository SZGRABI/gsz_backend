package cz.rohlik.gsz.repository;

import cz.rohlik.gsz.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
