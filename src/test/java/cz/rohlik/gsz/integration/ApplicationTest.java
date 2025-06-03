package cz.rohlik.gsz.integration;

import cz.rohlik.gsz.dto.OrderDTO;
import cz.rohlik.gsz.dto.OrderItemDTO;
import cz.rohlik.gsz.dto.ProductDTO;
import cz.rohlik.gsz.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    public void testCreateOrder() {
        ProductDTO bread = new ProductDTO(123L, "Bread", 120, BigDecimal.valueOf(96.49));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductDTO> request = new HttpEntity<>(bread, headers);
        // Create product
        ResponseEntity<ProductDTO> response = restTemplate.postForEntity(baseUrl() + "/product", request, ProductDTO.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody().getId(), is(1L));
        assertThat(response.getBody().getName(), is("Bread"));
        assertThat(response.getBody().getQuantityInStock(), is(120));
        assertThat(response.getBody().getPrice(), is(BigDecimal.valueOf(96.49)));

        List<OrderItemDTO> orderItems = new ArrayList<>();
        OrderItemDTO orderItem = OrderItemDTO.builder().productName("Bread").quantity(20).build();
        orderItems.add(orderItem);
        OrderDTO order = OrderDTO.builder().orderItems(orderItems).build();
        HttpEntity<OrderDTO> requestOrder = new HttpEntity<>(order, headers);
        // Create order
        ResponseEntity<OrderDTO> responseOrder = restTemplate.postForEntity(baseUrl() + "/order", requestOrder, OrderDTO.class);

        assertThat(responseOrder.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseOrder.getBody(), notNullValue());
        assertThat(responseOrder.getBody().getId(), is(1L));
        assertThat(responseOrder.getBody().getOrderItems(), not(empty()));
        assertThat(responseOrder.getBody().getStatus(), is(OrderStatus.NEW));

        // Get products and check if getQuantityInStock decreased
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<ProductDTO[]> responseProducts = restTemplate.exchange(
                baseUrl() + "/product",
                HttpMethod.GET,
                requestEntity,
                ProductDTO[].class
        );

        assertThat(responseProducts.getStatusCode(), is(HttpStatus.OK));
        ProductDTO[] products = responseProducts.getBody();
        assertThat(products, notNullValue());
        assertThat(products.length, is(1));
        assertThat(products[0].getQuantityInStock(), is(100));

        HttpEntity<Void> cancelRequest = new HttpEntity<>(null, headers);
        ResponseEntity<OrderDTO> cancelResponse = restTemplate.exchange(
                baseUrl() + "/order/" + responseOrder.getBody().getId() + "/cancel",
                HttpMethod.PUT,
                cancelRequest,
                OrderDTO.class);

        assertThat(cancelResponse.getStatusCode(), is(HttpStatus.OK));
        OrderDTO cancelledOrder = cancelResponse.getBody();
        assertThat(cancelledOrder, notNullValue());
        assertThat(cancelledOrder.getId(), is(responseOrder.getBody().getId()));
        assertThat(cancelledOrder.getStatus(), is(OrderStatus.CANCELLED));


    }
}
