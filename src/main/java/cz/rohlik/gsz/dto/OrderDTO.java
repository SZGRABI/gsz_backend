package cz.rohlik.gsz.dto;

import cz.rohlik.gsz.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDTO {
    long id;

    OrderStatus status;

    @Size(min = 1)
    List<@Valid OrderItemDTO> orderItems;
}
