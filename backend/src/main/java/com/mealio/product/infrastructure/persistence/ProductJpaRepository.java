package com.mealio.product.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findByGtin(String gtin);

    @Query("""
            select new com.mealio.product.infrastructure.persistence.CurrentProductRow(p, v)
            from ProductEntity p join ProductVersionEntity v
            on v.productId = p.id and v.versionNumber = p.currentVersion
            where lower(v.name) like :pattern escape '!'
            order by lower(v.name), p.id
            """)
    List<CurrentProductRow> search(@Param("pattern") String pattern, Pageable pageable);
}
