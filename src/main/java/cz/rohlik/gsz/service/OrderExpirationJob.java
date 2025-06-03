package cz.rohlik.gsz.service;

import cz.rohlik.gsz.entity.Order;
import cz.rohlik.gsz.entity.OrderItem;
import cz.rohlik.gsz.entity.OrderStatus;
import cz.rohlik.gsz.entity.Product;
import cz.rohlik.gsz.repository.OrderRepository;
import cz.rohlik.gsz.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderExpirationJob {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderExpirationJob(
            OrderRepository orderRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Runs once every 60 seconds.
     * Finds orders still in NEW status whose expiresAt < now,
     * marks them CANCELLED, and returns stock to each product.
     */
    @Scheduled(fixedRate = 60_000)  // 60,000 ms = 60 seconds
    @Transactional
    public void expireUnpaidOrders() {
        LocalDateTime now = LocalDateTime.now();

        List<Order> expired = orderRepository
                .findAllByStatusAndExpiresAtBefore(OrderStatus.NEW, now);

        for (Order order : expired) {
            for (OrderItem oi : order.getOrderItems()) {
                Product p = oi.getProduct();
                p.setQuantityInStock(p.getQuantityInStock() + oi.getQuantity());
                productRepository.save(p);
            }

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }
}
