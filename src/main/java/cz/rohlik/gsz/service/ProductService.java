package cz.rohlik.gsz.service;

import cz.rohlik.gsz.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDTO);

    void deleteProduct(Long id);

    ProductDTO updateProduct(ProductDTO productDTO);

    List<ProductDTO> getAllProducts();
}
