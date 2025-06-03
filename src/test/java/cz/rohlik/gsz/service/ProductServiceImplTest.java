package cz.rohlik.gsz.service;

import cz.rohlik.gsz.dto.ProductDTO;
import cz.rohlik.gsz.entity.Order;
import cz.rohlik.gsz.entity.OrderItem;
import cz.rohlik.gsz.entity.OrderStatus;
import cz.rohlik.gsz.entity.Product;
import cz.rohlik.gsz.exception.ProductAlreadyExistException;
import cz.rohlik.gsz.mapper.ProductMapper;
import cz.rohlik.gsz.mapper.ProductMapperImpl;
import cz.rohlik.gsz.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static cz.rohlik.gsz.service.ProductServiceImpl.PRODUCT_WITH_NAME_S_ALREADY_EXISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductMapper productMapper = new ProductMapperImpl();

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct() {
        ProductDTO productDTO = new ProductDTO(123L, "Bread", 100, BigDecimal.valueOf(34.5));
        Product savedProduct = new Product(1L, "Bread", 100, BigDecimal.valueOf(34.5), null);
        when(productRepository.save(any())).thenReturn(savedProduct);

        ProductDTO createdProduct = productService.createProduct(productDTO);

        assertNotNull(createdProduct);
        assertThat(createdProduct.getId(), not(productDTO.getId()));
        assertThat(createdProduct.getName(), is(productDTO.getName()));
        assertThat(createdProduct.getQuantityInStock(), is(productDTO.getQuantityInStock()));
        assertThat(createdProduct.getPrice(), is(productDTO.getPrice()));
    }

    @Test
    void deleteProduct() {
        Long productId = 1L;

        List<OrderItem> orderItems = new ArrayList<>();
        Product product = new Product(productId, "Bread", 100, BigDecimal.valueOf(34.5), orderItems);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(3);
        orderItem.setOrder(new Order(1L, OrderStatus.NEW, orderItems));
        orderItems.add(orderItem);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void updateProduct() {
        Product product = new Product(1L, "Bread", 100, BigDecimal.valueOf(75), null);
        ProductDTO newProductDto = new ProductDTO(1L, "Sourdough Bread", 120, BigDecimal.valueOf(99.99));

        when(productRepository.findById(newProductDto.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        ProductDTO updatedProduct = productService.updateProduct(newProductDto);

        assertNotNull(updatedProduct);
        assertThat(updatedProduct.getName(), is(updatedProduct.getName()));
    }

    @Test
    void updateProductExistWithSameName() {
        Product product = new Product(1L, "Bread", 100, BigDecimal.valueOf(75), null);
        Product existingProduct = new Product(2L, "Sourdough Bread", 100, BigDecimal.valueOf(75), null);
        ProductDTO newProductDto = new ProductDTO(1L, "Sourdough Bread", 120, BigDecimal.valueOf(99.99));

        when(productRepository.findById(newProductDto.getId())).thenReturn(Optional.of(product));
        when(productRepository.findByName(newProductDto.getName())).thenReturn(Optional.of(existingProduct));

        ProductAlreadyExistException thrown = assertThrows(ProductAlreadyExistException.class, () -> productService.updateProduct(newProductDto));

        assertNotNull(thrown);
        assertThat(thrown.getMessage(), is(PRODUCT_WITH_NAME_S_ALREADY_EXISTS.formatted(newProductDto.getName())));

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductIdIsNUll() {
        ProductDTO newProductDto = new ProductDTO(null, "Sourdough Bread", 120, BigDecimal.valueOf(99.99));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(newProductDto));

        assertNotNull(thrown);
        assertThat(thrown.getMessage(), is("Product id cannot be null"));

        verify(productRepository, never()).save(any());
    }

    @Test
    void getAllProducts() {
        List<Product> productList = Arrays.asList(
                new Product(1L, "Bread", 100, BigDecimal.valueOf(34.5), null),
                new Product(2L, "Beer", 50, BigDecimal.valueOf(45.5), null),
                new Product(3L, "Ham", 76, BigDecimal.valueOf(19), null)
        );

        when(productRepository.findAll()).thenReturn(productList);

        List<ProductDTO> returnedList = productService.getAllProducts();

        assertNotNull(returnedList);
        assertEquals(3, returnedList.size());
    }
}