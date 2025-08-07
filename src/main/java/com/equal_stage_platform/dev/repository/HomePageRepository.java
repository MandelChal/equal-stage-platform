package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.HomePageBanner;
import com.equal_stage_platform.dev.model.enums.BannerObjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HomePageRepository extends JpaRepository<HomePageBanner, Long> {
    
    List<HomePageBanner> findByObjectType(BannerObjectType objectType);
    
    List<HomePageBanner> findAllByOrderByDisplayOrderAsc();
    
    Optional<HomePageBanner> findByUrl(String url);
    
    Optional<HomePageBanner> findByDisplayOrder(Integer displayOrder);
    
    @Query("SELECT COALESCE(MAX(b.displayOrder), 0) FROM HomePageBanner b")
    Integer findMaxDisplayOrder();
    
    @Query("SELECT b FROM HomePageBanner b WHERE b.displayOrder > :displayOrder ORDER BY b.displayOrder ASC")
    List<HomePageBanner> findAllWithDisplayOrderGreaterThan(@Param("displayOrder") Integer displayOrder);
    
    @Query("SELECT b FROM HomePageBanner b WHERE b.displayOrder >= :fromOrder AND b.displayOrder <= :toOrder ORDER BY b.displayOrder ASC")
    List<HomePageBanner> findAllByDisplayOrderBetween(@Param("fromOrder") Integer fromOrder, @Param("toOrder") Integer toOrder);
    
    boolean existsByDisplayOrder(Integer displayOrder);
}