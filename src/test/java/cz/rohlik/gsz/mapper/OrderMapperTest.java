package cz.rohlik.gsz.mapper;

import cz.rohlik.gsz.dto.OrderDTO;
import cz.rohlik.gsz.dto.OrderItemDTO;
import cz.rohlik.gsz.entity.Order;
import cz.rohlik.gsz.entity.OrderItem;
import cz.rohlik.gsz.entity.OrderStatus;
import cz.rohlik.gsz.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderMapperTest {

    public static final Product BREAD = new Product();

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderMapperImpl orderMapper = new OrderMapperImpl();

    private static Order entity;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        entity = new Order();
        entity.setStatus(OrderStatus.CANCELLED);
        setEntityId(1L);

        BREAD.setName("bread");
        BREAD.setQuantityInStock(100);
        BREAD.setPrice(BigDecimal.valueOf(100.1));

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem breadOrder = new OrderItem();
        breadOrder.setQuantity(2);
        breadOrder.setProduct(BREAD);
        orderItems.add(breadOrder);
        entity.setOrderItems(orderItems);
    }

    static void setEntityId(Long id) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getDeclaredField("id");
        if (idField.trySetAccessible()) {
            idField.set(entity, id);
        }
    }

    @Test
    void toDTO() {
        OrderItemDTO breadDto = new OrderItemDTO();
        breadDto.setProductName("bread");
        breadDto.setQuantity(3);
        breadDto.setId(4L);

        when(orderItemMapper.toDTO(Mockito.any(OrderItem.class)))
                .thenReturn(breadDto);

        OrderDTO resultDto = orderMapper.toDTO(entity);

        assertThat(resultDto, notNullValue());
        assertThat(resultDto.getId(), notNullValue());
        assertThat(resultDto.getId(), is(entity.getId()));
        assertThat(resultDto.getOrderItems(), notNullValue());
        assertThat(resultDto.getOrderItems().size(), is(1));
        assertThat(resultDto.getOrderItems().get(0), notNullValue());
        assertThat(resultDto.getOrderItems().get(0).getProductName(), notNullValue());
        assertThat(resultDto.getOrderItems().get(0).getProductName(), is("bread"));
        assertThat(resultDto.getStatus(), is(OrderStatus.CANCELLED));
    }

    @Test
    void toEntity() {
        OrderDTO dto = new OrderDTO();
        dto.setId(1L);
        dto.setStatus(OrderStatus.PAID);
        List<OrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemDTO());
        dto.setOrderItems(orderItems);

        OrderItem breadEntity = new OrderItem();
        breadEntity.setProduct(BREAD);
        breadEntity.setQuantity(3);

        when(orderItemMapper.toEntity(Mockito.any(OrderItemDTO.class)))
                .thenReturn(breadEntity);


        Order resultEntity = orderMapper.toEntity(dto);

        assertThat(resultEntity, notNullValue());
        assertThat(resultEntity.getId(), nullValue());
        assertThat(resultEntity.getOrderItems(), notNullValue());
        assertThat(resultEntity.getOrderItems().size(), is(1));
        assertThat(resultEntity.getOrderItems().get(0), notNullValue());
        assertThat(resultEntity.getOrderItems().get(0).getProduct(), notNullValue());
        assertThat(resultEntity.getOrderItems().get(0).getProduct().getName(), notNullValue());
        assertThat(resultEntity.getOrderItems().get(0).getProduct().getName(), is("bread"));
        assertThat(resultEntity.getStatus(), is(OrderStatus.NEW));
    }

    @Test
    public void updateEntityFromDto() {
        OrderDTO dto = new OrderDTO();
        dto.setId(1L);
        dto.setStatus(OrderStatus.PAID);
        List<OrderItemDTO> orderItems = new ArrayList<>();
        OrderItemDTO breadOrder = new OrderItemDTO();
        breadOrder.setQuantity(2);
        breadOrder.setProductName("bread");
        breadOrder.setId(4L);
        orderItems.add(breadOrder);
        dto.setOrderItems(orderItems);

        orderMapper.updateEntityFromDto(dto, entity);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), notNullValue());
        assertThat(entity.getOrderItems(), notNullValue());
        assertThat(entity.getOrderItems().size(), is(1));
        assertThat(entity.getOrderItems().get(0), notNullValue());
        assertThat(entity.getOrderItems().get(0).getProduct(), notNullValue());
        assertThat(entity.getOrderItems().get(0).getProduct().getName(), notNullValue());
        assertThat(entity.getOrderItems().get(0).getProduct().getName(), is("bread"));
        assertThat(entity.getStatus(), is(OrderStatus.PAID));
    }
}
