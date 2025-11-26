package org.ecom.liftify.controller;

import jakarta.validation.Valid;
import org.ecom.liftify.dto.request.product.CreateProductRequest;
import org.ecom.liftify.dto.request.product.UpdateProductRequest;
import org.ecom.liftify.dto.response.product.ProductListItemResponse;
import org.ecom.liftify.dto.response.product.ProductResponse;
import org.ecom.liftify.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest product) {
        ProductResponse productResponse = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        ProductResponse productResponse = productService.getProductById(id);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping
    public ResponseEntity<List<ProductListItemResponse>> getProducts(
            @RequestParam(required = false) String search
    ) {
        List<ProductListItemResponse> products = search != null && !search.isEmpty()
                ? productService.searchProduct(search)
                : productService.getProductList();

        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<List<ProductListItemResponse>> getProductsByCategory(
            @RequestParam(required = false) String category
    ) {
        List<ProductListItemResponse> products = productService.searchProductByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductListItemResponse>> filterByPrice(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max
    ) {
        List<ProductListItemResponse> products = productService.getProductListByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest product
    ) {
        ProductResponse productResponse = productService.updateProduct(id, product);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam Integer change
    ) {
        productService.updateStock(id, change);
        return ResponseEntity.ok().build();
    }
}
