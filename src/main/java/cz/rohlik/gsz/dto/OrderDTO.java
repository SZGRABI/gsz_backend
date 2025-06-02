package cz.rohlik.gsz.dto;

import cz.rohlik.gsz.entity.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    long id;
    OrderStatus status;
    List<OrderItemDTO> orderItems;
}
