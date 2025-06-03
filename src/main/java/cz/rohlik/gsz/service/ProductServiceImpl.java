package cz.rohlik.gsz.service;

import cz.rohlik.gsz.dto.ProductDTO;
import cz.rohlik.gsz.entity.OrderStatus;
import cz.rohlik.gsz.entity.Product;
import cz.rohlik.gsz.exception.ProductAlreadyExistException;
import cz.rohlik.gsz.exception.ProductCannotBeDeleted;
import cz.rohlik.gsz.exception.ProductNotFoundException;
import cz.rohlik.gsz.mapper.ProductMapper;
import cz.rohlik.gsz.repository.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional
public class ProductServiceImpl implements ProductService {
    static final String PRODUCT_WITH_NAME_S_ALREADY_EXISTS = "product with name %s already exists";
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDTO createProduct(@Valid ProductDTO productDTO) {
        Optional<Product> foundProduct = productRepository.findByName(productDTO.getName());
        if (foundProduct.isPresent()) {
            throw new ProductAlreadyExistException(PRODUCT_WITH_NAME_S_ALREADY_EXISTS.formatted(productDTO.getName()));
        }
        Product product = productRepository.save(productMapper.toEntity(productDTO));

        return productMapper.toDTO(product);
    }

    @Override
    public void deleteProduct(@Positive Long id) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id %d not found".formatted(id)));

        if (existingProduct.getOrderItems() != null && !existingProduct.getOrderItems().isEmpty() && productRepository.existsByNameAndOrderItems_Order_Status(existingProduct.getName(), OrderStatus.NEW)) {
            throw new ProductCannotBeDeleted("Product with id %d has active orders".formatted(id));
        }

        productRepository.delete(existingProduct);
    }

    @Override
    public ProductDTO updateProduct(@Valid ProductDTO productUpdateDTO) {
        if (productUpdateDTO.getId() == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        final Product foundProduct = productRepository.findById(productUpdateDTO.getId()).orElseThrow(() -> new ProductNotFoundException("Product with name %s not found".formatted(productUpdateDTO.getName())));

        if (!foundProduct.getName().equals(productUpdateDTO.getName())) {
            if (productRepository.findByName(productUpdateDTO.getName()).isPresent()) {
                throw new ProductAlreadyExistException(PRODUCT_WITH_NAME_S_ALREADY_EXISTS.formatted(productUpdateDTO.getName()));
            }
            foundProduct.setName(productUpdateDTO.getName());
        }

        foundProduct.setPrice(productUpdateDTO.getPrice());
        foundProduct.setQuantityInStock(productUpdateDTO.getQuantityInStock());

        Product updatedProduct = productRepository.save(foundProduct);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toDTO).toList();
    }
}
