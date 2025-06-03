package cz.rohlik.gsz.service;

import cz.rohlik.gsz.dto.ProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface ProductService {

    ProductDTO createProduct(@Valid ProductDTO productDTO);

    void deleteProduct(@Positive Long id);

    ProductDTO updateProduct(@Valid ProductDTO productUpdateDTO);

    List<ProductDTO> getAllProducts();
}
