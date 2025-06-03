package cz.rohlik.gsz.mapper;

import cz.rohlik.gsz.dto.OrderItemDTO;
import cz.rohlik.gsz.entity.Order;
import cz.rohlik.gsz.entity.OrderItem;
import cz.rohlik.gsz.entity.Product;
import cz.rohlik.gsz.exception.ProductNotFoundException;
import cz.rohlik.gsz.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemMapperTest {

    public static final String PRODUCT_NAME = "Bread";
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private final OrderItemMapper orderItemMapper = new OrderItemMapperImpl();

    private static OrderItem entity;
    private static OrderItemDTO dto;
    private static Product product;

    @BeforeAll
    public static void setUp() {
        entity = new OrderItem();

        entity.setQuantity(3);

        product = new Product();
        product.setName(PRODUCT_NAME);
        product.setQuantityInStock(100);
        product.setPrice(BigDecimal.valueOf(100.1));
        entity.setProduct(product);

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(entity);
        order.setOrderItems(orderItems);
        entity.setOrder(order);

        dto = new OrderItemDTO();
        dto.setId(1L);
        dto.setQuantity(3);
        dto.setProductName("Bread");

    }

    @Test
    void toDTO() throws NoSuchFieldException, IllegalAccessException {
        entity.setId(1L);

        OrderItemDTO resultDto = orderItemMapper.toDTO(entity);

        assertThat(resultDto, notNullValue());
        assertThat(resultDto.getId(), is(1L));
        assertThat(resultDto.getQuantity(), is(entity.getQuantity()));
        assertThat(resultDto.getProductName(), is(PRODUCT_NAME));

    }

    @Test
    void toEntity() {
        when(productRepository.findByName(PRODUCT_NAME)).thenReturn(Optional.of(product));

        OrderItem resultEntity = orderItemMapper.toEntity(dto);

        assertThat(resultEntity, notNullValue());
        assertThat(resultEntity.getId(), nullValue());
        assertThat(resultEntity.getQuantity(), is(entity.getQuantity()));
        assertThat(resultEntity.getProduct(), is(entity.getProduct()));
        assertThat(resultEntity.getOrder(), nullValue());
    }

    @Test
    void toEntity_ProductNotFound() {
        when(productRepository.findByName(PRODUCT_NAME)).thenReturn(Optional.empty());

        ProductNotFoundException thrown = assertThrows(ProductNotFoundException.class, () -> orderItemMapper.toEntity(dto));

        assertThat(thrown, notNullValue());
        assertThat(thrown.getMessage(), notNullValue());
        assertThat(thrown.getMessage(), is("Product:%s does not exist.".formatted(PRODUCT_NAME)));
    }
}