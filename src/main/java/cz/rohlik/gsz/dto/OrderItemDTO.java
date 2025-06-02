package cz.rohlik.gsz.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    long id;
    int quantity;
    String productName;
}
