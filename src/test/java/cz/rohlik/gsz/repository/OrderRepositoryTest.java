package cz.rohlik.gsz.repository;

import cz.rohlik.gsz.entity.Order;
import cz.rohlik.gsz.entity.OrderItem;
import cz.rohlik.gsz.entity.OrderStatus;
import cz.rohlik.gsz.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void findAllEmpty() {
        List<Order> orders = orderRepository.findAll();
        assertThat(orders, is(empty()));
    }

    @Test
    void saveOrderWithItems() {
        Product product = new Product();
        product.setName("Bread");
        product.setQuantityInStock(100);
        product.setPrice(BigDecimal.valueOf(79.19));
        product = productRepository.saveAndFlush(product);

        OrderItem item1 = new OrderItem();
        item1.setQuantity(2);
        item1.setProduct(product);

        OrderItem item2 = new OrderItem();
        item2.setQuantity(3);
        item2.setProduct(product);

        Order order = new Order();
        order.addOrderItem(item1);
        order.addOrderItem(item2);

        order = orderRepository.saveAndFlush(order);

        assertThat(order.getId(), notNullValue());
        List<Order> orders = orderRepository.findAll();
        assertThat(orders, hasSize(1));

        Order actualOrder = orders.get(0);
        assertThat(actualOrder.getExpiresAt(), notNullValue());
        assertThat(actualOrder.getOrderItems(), hasSize(2));

        for (OrderItem orderItem : actualOrder.getOrderItems()) {
            assertThat(orderItem.getId(), notNullValue());
            assertThat(orderItem.getProduct().getId(), is(product.getId()));
            assertThat(orderItem.getQuantity(), greaterThan(0));
        }

        assertThat(orderItemRepository.findAll(), hasSize(2));
    }

    @Test
    void orphanRemovalRemovesItem() {
        Product p = new Product();
        p.setName("Beer");
        p.setQuantityInStock(50);
        p.setPrice(BigDecimal.valueOf(49.99));
        p = productRepository.saveAndFlush(p);

        OrderItem i1 = new OrderItem();
        i1.setQuantity(1);
        i1.setProduct(p);
        OrderItem i2 = new OrderItem();
        i2.setQuantity(4);
        i2.setProduct(p);

        Order o = new Order();
        o.addOrderItem(i1);
        o.addOrderItem(i2);
        o = orderRepository.saveAndFlush(o);

        OrderItem toRemove = o.getOrderItems().get(0);
        o.removeOrderItem(toRemove);
        orderRepository.saveAndFlush(o);


        Order refreshed = orderRepository.findById(o.getId()).orElseThrow();
        assertThat(refreshed.getOrderItems(), hasSize(1));

        assertThat(orderItemRepository.findById(toRemove.getId()).isPresent(), is(false));
    }

    @Test
    void deleteOrderAlsoDeletesItems() {
        Product prod = new Product();
        prod.setName("Milk");
        prod.setQuantityInStock(20);
        prod.setPrice(BigDecimal.valueOf(40.41));
        prod = productRepository.saveAndFlush(prod);

        OrderItem oi = new OrderItem();
        oi.setQuantity(5);
        oi.setProduct(prod);

        Order ord = new Order();
        ord.addOrderItem(oi);
        ord = orderRepository.saveAndFlush(ord);

        Long itemId = oi.getId();
        Long orderId = ord.getId();

        orderRepository.deleteById(orderId);
        orderRepository.flush();

        assertThat(orderRepository.findById(orderId).isPresent(), is(false));
        assertThat(orderItemRepository.findById(itemId).isPresent(), is(false));
    }

    @Test
    void payOrderChangesStatusToPaid() {
        Product p = new Product();
        p.setName("Beer");
        p.setQuantityInStock(5);
        p.setPrice(BigDecimal.valueOf(52.24));
        productRepository.saveAndFlush(p);

        OrderItem item = new OrderItem();
        item.setProduct(p);
        item.setQuantity(2);

        Order order = new Order();
        order.addOrderItem(item);
        order = orderRepository.saveAndFlush(order);

        assertThat(order.getStatus(), is(OrderStatus.NEW));

        order.pay();
        order = orderRepository.saveAndFlush(order);

        Order reloaded = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getStatus(), is(OrderStatus.PAID));
    }

    @Test
    void cannotPayTwice() {
        Product p = new Product();
        p.setName("Beer");
        p.setQuantityInStock(3);
        p.setPrice(BigDecimal.valueOf(39.99));
        productRepository.saveAndFlush(p);

        OrderItem item = new OrderItem();
        item.setProduct(p);
        item.setQuantity(1);

        Order order = new Order();
        order.addOrderItem(item);
        order = orderRepository.saveAndFlush(order);

        order.pay();
        order = orderRepository.saveAndFlush(order);

        Order alreadyPaid = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(alreadyPaid.getStatus(), is(OrderStatus.PAID));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            alreadyPaid.pay();
            orderRepository.saveAndFlush(alreadyPaid);
        });

        assertThat(thrown, notNullValue());
        assertThat(thrown.getMessage(), containsString(Order.PAY_ERROR_MESSAGE.formatted(order.getStatus())));
    }

}