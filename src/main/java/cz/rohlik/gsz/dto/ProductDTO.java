package cz.rohlik.gsz.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    long id;
    String name;
    int quantityInStock;
    BigDecimal price;
}
