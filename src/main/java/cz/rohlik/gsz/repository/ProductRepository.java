package cz.rohlik.gsz.repository;

import cz.rohlik.gsz.entity.OrderStatus;
import cz.rohlik.gsz.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    boolean existsByNameAndOrderItems_Order_Status(String name, OrderStatus orderStatus);
}
