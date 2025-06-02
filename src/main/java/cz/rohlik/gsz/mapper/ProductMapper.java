package cz.rohlik.gsz.mapper;

import cz.rohlik.gsz.dto.ProductDTO;
import cz.rohlik.gsz.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "name", source = "product.name")
    ProductDTO toDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Product toEntity(ProductDTO productDTO);

}
