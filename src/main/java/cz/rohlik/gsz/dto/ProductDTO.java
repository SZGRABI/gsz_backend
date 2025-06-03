package cz.rohlik.gsz.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    Long id;

    @NotBlank(message = "Name must not be blank")
    String name;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be ≥ 0")
    int quantityInStock;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be > 0")
    BigDecimal price;
}
