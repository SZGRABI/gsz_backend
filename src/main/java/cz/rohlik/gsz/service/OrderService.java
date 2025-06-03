package cz.rohlik.gsz.service;

import cz.rohlik.gsz.dto.OrderDTO;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);

    OrderDTO cancelOrder(Long orderId);

    OrderDTO payOrder(Long orderId);
}
