package cz.rohlik.gsz.service;

import cz.rohlik.gsz.dto.OrderDTO;
import cz.rohlik.gsz.entity.Order;
import cz.rohlik.gsz.entity.OrderItem;
import cz.rohlik.gsz.entity.OrderStatus;
import cz.rohlik.gsz.entity.Product;
import cz.rohlik.gsz.exception.*;
import cz.rohlik.gsz.mapper.OrderMapper;
import cz.rohlik.gsz.repository.OrderRepository;
import cz.rohlik.gsz.repository.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
@Validated
@Transactional
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDTO createOrder(@Valid OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);

        for (OrderItem orderItem : order.getOrderItems()) {
            String orderedProductName = orderItem.getProduct().getName();
            Product managedProduct =
                    productRepository.findByName(orderedProductName).orElseThrow(() -> new ProductNotFoundException("Product not found!"));

            int quantityInStock = managedProduct.getQuantityInStock();
            int orderedQuantity = orderItem.getQuantity();
            if (quantityInStock < orderedQuantity) {
                throw new OutOfStockException("Ordered %d %s but only %d are available".formatted(orderedQuantity, orderedProductName, quantityInStock));
            }
            managedProduct.setQuantityInStock(quantityInStock - orderedQuantity);
            productRepository.save(managedProduct);

            orderItem.setProduct(managedProduct);
            orderItem.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    public OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("There is no order with id %d".formatted(orderId)));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new OrderCancelException("%s order cannot be cancelled".formatted(order.getStatus()));
        }

        order.setStatus(OrderStatus.CANCELLED);
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setQuantityInStock(product.getQuantityInStock() + orderItem.getQuantity());
        }

        orderRepository.save(order);

        return orderMapper.toDTO(order);

    }

    @Override
    public OrderDTO payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("There is no order with id %d".formatted(orderId)));
        if (order.getStatus() != OrderStatus.NEW) {
            throw new OrderPaymentException("%s order cannot be paid".formatted(order.getStatus()));
        }
        if (order.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OrderPaymentException("Order has expired and cannot be paid");
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return orderMapper.toDTO(order);
    }
}
