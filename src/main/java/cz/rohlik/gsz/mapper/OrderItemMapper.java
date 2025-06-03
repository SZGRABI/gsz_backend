package cz.rohlik.gsz.mapper;

import cz.rohlik.gsz.dto.OrderItemDTO;
import cz.rohlik.gsz.entity.OrderItem;
import cz.rohlik.gsz.repository.ProductRepository;
import cz.rohlik.gsz.exception.ProductNotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class OrderItemMapper {
    @Autowired
    protected ProductRepository productRepository;

    @Mappings({
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "id", target = "id")
    })
    abstract OrderItemDTO toDTO(OrderItem orderItem);

    @Mappings({
            @Mapping(target = "product",
                    expression = "java(productRepository.findByName(orderItemDTO.getProductName()).orElseThrow(() -> new ProductNotFoundException(\"Product:%s does not exist.\".formatted(orderItemDTO.getProductName()))))"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "order", ignore = true)})
    abstract OrderItem toEntity(OrderItemDTO orderItemDTO) throws ProductNotFoundException;
}
