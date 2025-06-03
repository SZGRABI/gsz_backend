package cz.rohlik.gsz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderItemDTO {
    long id;
    @Min(value = 1, message = "At least 1 product need to be ordered ")
    int quantity;
    @NotBlank(message = "Product must be chosen")
    String productName;
}
