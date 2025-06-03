package cz.rohlik.gsz.controller;

import cz.rohlik.gsz.dto.ProductDTO;
import cz.rohlik.gsz.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "List all products")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAll() {
        List<ProductDTO> all = productService.getAllProducts();
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "Create a product")
    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO payload) {
        ProductDTO created = productService.createProduct(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update a product")
    @PutMapping
    public ResponseEntity<ProductDTO> update(@Valid @RequestBody ProductDTO payload) {
        ProductDTO updated = productService.updateProduct(payload);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
