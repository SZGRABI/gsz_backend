package cz.rohlik.gsz.service;

import cz.rohlik.gsz.dto.OrderDTO;
import jakarta.validation.Valid;

public interface OrderService {
    OrderDTO createOrder(@Valid OrderDTO orderDTO);

    OrderDTO cancelOrder(Long orderId);

    OrderDTO payOrder(Long orderId);
}
