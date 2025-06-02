package cz.rohlik.gsz.mapper;

import cz.rohlik.gsz.dto.ProductDTO;
import cz.rohlik.gsz.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapperImpl();

    @Test
    void toDTO() {
        Product entity = new Product();
        entity.setPrice(BigDecimal.valueOf(1.23));
        entity.setName("Bread");
        entity.setQuantityInStock(12);

        ProductDTO resultDto = productMapper.toDTO(entity);

        assertThat(resultDto, notNullValue());
        assertThat(resultDto.getId(), notNullValue());
        assertThat(resultDto.getName(), notNullValue());
        assertThat(resultDto.getName(), is(entity.getName()));
        assertThat(resultDto.getPrice(), notNullValue());
        assertThat(resultDto.getPrice(), is(entity.getPrice()));
        assertThat(resultDto.getQuantityInStock(), notNullValue());
        assertThat(resultDto.getQuantityInStock(), is(entity.getQuantityInStock()));
    }

    @Test
    void toEntity() {
        ProductDTO dto = new ProductDTO();
        dto.setPrice(BigDecimal.valueOf(1.23));
        dto.setName("Bread");
        dto.setQuantityInStock(12);

        Product resultEntity = productMapper.toEntity(dto);

        assertThat(resultEntity, notNullValue());
        assertThat(resultEntity.getId(), nullValue());
        assertThat(resultEntity.getName(), notNullValue());
        assertThat(resultEntity.getName(), is(dto.getName()));
        assertThat(resultEntity.getPrice(), notNullValue());
        assertThat(resultEntity.getPrice(), is(dto.getPrice()));
        assertThat(resultEntity.getQuantityInStock(), notNullValue());
        assertThat(resultEntity.getQuantityInStock(), is(dto.getQuantityInStock()));
    }
}