package cz.rohlik.gsz.service;

import cz.rohlik.gsz.dto.OrderDTO;
import cz.rohlik.gsz.entity.Order;
import cz.rohlik.gsz.entity.OrderItem;
import cz.rohlik.gsz.entity.OrderStatus;
import cz.rohlik.gsz.entity.Product;
import cz.rohlik.gsz.exception.OrderCancelException;
import cz.rohlik.gsz.exception.OrderNotFoundException;
import cz.rohlik.gsz.exception.OrderPaymentException;
import cz.rohlik.gsz.exception.OutOfStockException;
import cz.rohlik.gsz.mapper.OrderMapper;
import cz.rohlik.gsz.repository.OrderRepository;
import cz.rohlik.gsz.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder() {
        Product bread = Product.builder().id(1L).name("bread").quantityInStock(3).price(BigDecimal.valueOf(70.15)).build();
        Product beer = Product.builder().id(2L).name("beer").quantityInStock(2).price(BigDecimal.valueOf(34.1)).build();

        OrderItem breadOrder = OrderItem.builder()
                .id(1L)
                .product(bread)
                .quantity(2)
                .build();

        OrderItem beerOrder = OrderItem.builder()
                .id(2L)
                .product(beer)
                .quantity(2)
                .build();

        List<OrderItem> orderItems = Arrays.asList(breadOrder, beerOrder);
        Order order = Order.builder().id(1L).orderItems(orderItems).build();

        when(orderMapper.toEntity(any())).thenReturn(order);
        when(productRepository.findByName(bread.getName())).thenReturn(Optional.of(bread));
        when(productRepository.findByName(beer.getName())).thenReturn(Optional.of(beer));

        orderService.createOrder(new OrderDTO());

        assertThat(bread.getQuantityInStock(), is(1));
        assertThat(beer.getQuantityInStock(), is(0));

        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrderOutOfStock() {
        Product bread = Product.builder().id(1L).name("bread").quantityInStock(3).price(BigDecimal.valueOf(70.15)).build();
        Product beer = Product.builder().id(2L).name("beer").quantityInStock(2).price(BigDecimal.valueOf(34.1)).build();

        List<OrderItem> orderItems = getOrderItems(bread, beer);
        Order order = Order.builder().id(1L).orderItems(orderItems).build();

        when(orderMapper.toEntity(any())).thenReturn(order);
        when(productRepository.findByName(bread.getName())).thenReturn(Optional.of(bread));
        when(productRepository.findByName(beer.getName())).thenReturn(Optional.of(beer));

        OutOfStockException thrown = assertThrows(OutOfStockException.class, () -> orderService.createOrder(new OrderDTO()));
        assertThat(thrown, notNullValue());
        assertThat(thrown.getMessage(), is("Ordered 3 beer but only 2 are available"));

        verify(orderRepository, never()).save(any(Order.class));
    }

    private static List<OrderItem> getOrderItems(Product bread, Product beer) {
        OrderItem breadOrder = OrderItem.builder()
                .id(1L)
                .product(bread)
                .quantity(2)
                .build();

        OrderItem beerOrder = OrderItem.builder()
                .id(2L)
                .product(beer)
                .quantity(3)
                .build();

        List<OrderItem> orderItems = Arrays.asList(breadOrder, beerOrder);
        return orderItems;
    }

    @Test
    void cancelOrder() {
        Product bread = Product.builder().id(1L).name("bread").quantityInStock(3).price(BigDecimal.valueOf(70.15)).build();
        Product beer = Product.builder().id(2L).name("beer").quantityInStock(2).price(BigDecimal.valueOf(34.1)).build();

        List<OrderItem> orderItems = getOrderItems(bread, beer);

        Order order = Order.builder().id(1L).orderItems(orderItems).status(OrderStatus.NEW).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertThat(order.getStatus(), is(OrderStatus.CANCELLED));
        assertThat(bread.getQuantityInStock(), is(5));
        assertThat(beer.getQuantityInStock(), is(5));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void cancelOrderNoOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        OrderNotFoundException thrown = assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(1L));

        assertThat(thrown, notNullValue());
        assertThat(thrown.getMessage(), is("There is no order with id 1"));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrderWrongStatus() {
        Order order = Order.builder().id(1L).status(OrderStatus.PAID).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderCancelException thrown = assertThrows(OrderCancelException.class, () -> orderService.cancelOrder(1L));

        assertThat(thrown, notNullValue());
        assertThat(thrown.getMessage(), is("PAID order cannot be cancelled"));

        verify(orderRepository, never()).save(any(Order.class));
    }


    @Test
    void payOrder() {
        Order order = Order.builder().id(1L)
                .expiresAt(LocalDateTime.now().plusMinutes(20))
                .status(OrderStatus.NEW).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.payOrder(order.getId());

        assertThat(order.getStatus(), is(OrderStatus.PAID));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void payOrderWrongStatus() {
        Order order = Order.builder().id(1L).status(OrderStatus.PAID).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderPaymentException thrown = assertThrows(OrderPaymentException.class, () -> orderService.payOrder(order.getId()));

        assertThat(thrown, notNullValue());
        assertThat(thrown.getMessage(), is("PAID order cannot be paid"));
    }

    @Test
    void payOrderExpired() {
        Order order = Order.builder().id(1L)
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .status(OrderStatus.NEW).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderPaymentException thrown = assertThrows(OrderPaymentException.class, () -> orderService.payOrder(order.getId()));

        assertThat(thrown, notNullValue());
        assertThat(thrown.getMessage(), is("Order has expired and cannot be paid"));
    }
}