package org.ecom.liftify.repository;

import org.ecom.liftify.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByTitle(String title);

    List<Product> findByTitleContainingIgnoreCase(String title);

    List<Product> findByPriceBetween(BigDecimal priceStart, BigDecimal priceEnd);

    List<Product> findByRemainingStockGreaterThan(Integer stock);

    Boolean existsByTitle(String title);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.productImages " +
            "WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
}
