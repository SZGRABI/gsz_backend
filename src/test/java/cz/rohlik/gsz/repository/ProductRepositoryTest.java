package cz.rohlik.gsz.repository;

import cz.rohlik.gsz.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void findAllEmpty() {
        List<Product> products = productRepository.findAll();
        assertThat(products, not(nullValue()));
        assertThat(products, is(empty()));
    }

    @Test
    public void saveProduct() {
        Product product = new Product();
        product.setName("Test");
        product.setQuantityInStock(50);
        product.setPrice(BigDecimal.valueOf(123.45));

        productRepository.save(product);

        List<Product> products = productRepository.findAll();
        assertThat(products, not(nullValue()));
        assertThat(products, hasSize(1));

        Product actualProduct = products.get(0);
        assertThat(actualProduct, not(nullValue()));
        assertThat(actualProduct.getId(), not(nullValue()));
        assertThat(actualProduct.getId(), not(0));
        assertThat(actualProduct.getName(), is(product.getName()));
        assertThat(actualProduct.getPrice(), comparesEqualTo(product.getPrice()));
        assertThat(actualProduct.getQuantityInStock(), is(product.getQuantityInStock()));
    }


    @Test
    void uniqueNameConstraint() {
        String uniqueName = "Unique";

        var p1 = new Product();
        p1.setName(uniqueName);
        p1.setQuantityInStock(10);
        p1.setPrice(BigDecimal.valueOf(9.99));
        productRepository.saveAndFlush(p1);

        var p2 = new Product();
        p2.setName(uniqueName);
        p2.setQuantityInStock(5);
        p2.setPrice(BigDecimal.valueOf(5.55));

        assertThrows(DataIntegrityViolationException.class, () -> productRepository.save(p2));
    }
}