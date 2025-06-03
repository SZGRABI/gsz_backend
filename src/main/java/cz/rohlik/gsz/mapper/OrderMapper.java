package cz.rohlik.gsz.mapper;

import cz.rohlik.gsz.dto.OrderDTO;
import cz.rohlik.gsz.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDTO toDTO(Order order);

    @Mappings({@Mapping(target = "id", ignore = true),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "orderItems", target = "orderItems")})
    Order toEntity(OrderDTO orderDTO);

    @Mappings({@Mapping(target = "id", ignore = true),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "orderItems", ignore = true)})
    void updateEntityFromDto(OrderDTO orderDTO, @MappingTarget Order order);
}
