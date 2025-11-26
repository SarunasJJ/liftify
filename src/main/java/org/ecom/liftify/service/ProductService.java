package org.ecom.liftify.service;

import org.ecom.liftify.dto.request.product.CreateProductRequest;
import org.ecom.liftify.dto.request.product.UpdateProductRequest;
import org.ecom.liftify.dto.response.product.ProductListItemResponse;
import org.ecom.liftify.dto.response.product.ProductResponse;
import org.ecom.liftify.entity.Product;
import org.ecom.liftify.entity.ProductImage;
import org.ecom.liftify.entity.Rating;
import org.ecom.liftify.exception.DuplicateResourceException;
import org.ecom.liftify.exception.ResourceNotFoundException;
import org.ecom.liftify.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        if(productRepository.existsByTitle(request.title())){
            throw new ResourceNotFoundException("Product with title " + request.title() + " already exists");
        }

        Product product = Product.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .remainingStock(request.remainingStock())
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    public ProductResponse  updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product does not exist"));

        if(request.title() != null){
            if(!request.title().equals(product.getTitle())
                && productRepository.existsByTitle(request.title())){
                throw new DuplicateResourceException("Product with such title already exists");
            }
            product.setTitle(request.title());
        }
        if(request.description() != null){
            product.setDescription(request.description());
        }
        if(request.price() != null){
            product.setPrice(request.price());
        }
        if(request.remainingStock() != null){
            product.setRemainingStock(request.remainingStock());
        }

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Product does not exist");
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdWithImages(id)
                .orElseThrow();
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductListItemResponse> getProductList(){
        return productRepository.findAll().stream()
                .map(this::mapToListItemResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductListItemResponse> searchProduct(String search) {
        return productRepository.findByTitleContainingIgnoreCase(search).stream()
                .map(this::mapToListItemResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductListItemResponse> searchProductByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToListItemResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductListItemResponse> getProductListByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max).stream()
                .map(this::mapToListItemResponse)
                .toList();
    }

    public void updateStock(Long id, Integer change) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product does not exist"));

        if(product.getRemainingStock() < change){
            throw new IllegalStateException("Stock cannot be below 0");
        }

        product.setRemainingStock(product.getRemainingStock() - change);
        productRepository.save(product);
    }

    private ProductResponse mapToResponse(Product product){
        Double averageRating = product.getRatings().isEmpty() ? null :
                product.getRatings().stream()
                        .mapToInt(Rating::getRating)
                        .average()
                        .orElse(0.0);

        List<String> imgUrls = product.getProductImages().stream()
                .map(ProductImage::getImgUrl)
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getRemainingStock(),
                averageRating,
                product.getRatings().size(),
                imgUrls
        );
    }

    private ProductListItemResponse mapToListItemResponse(Product product){
        Double averageRating = product.getRatings().isEmpty() ? null :
                product.getRatings().stream()
                        .mapToInt(Rating::getRating)
                        .average()
                        .orElse(0.0);

        String primaryImgUrl = product.getProductImages().isEmpty() ? null :
                product.getProductImages().getFirst().getImgUrl();

        return new ProductListItemResponse(
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getRemainingStock(),
                averageRating,
                primaryImgUrl
        );
    }
}
