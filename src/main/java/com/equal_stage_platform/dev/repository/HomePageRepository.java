package com.equal_stage_platform.dev.repository;

import com.equal_stage_platform.dev.model.HomePageBanner;
import com.equal_stage_platform.dev.model.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HomePageRepository extends JpaRepository<HomePageBanner, Integer> {
    
    List<HomePageBanner> findByMediaType(MediaType mediaType);
    
    List<HomePageBanner> findAllByOrderByPositionAsc();
    
    Optional<HomePageBanner> findByUrl(String url);
    
    Optional<HomePageBanner> findByPosition(Integer position);
    
    @Query("SELECT COALESCE(MAX(b.position), 0) FROM HomePageBanner b")
    Integer findMaxPosition();

    // shift the banners to the right
    @Query("UPDATE HomePageBanner b SET b.position = b.position + 1 WHERE b.position >= :position")
    @Modifying
    void shiftBannersToRight(@Param("position") Integer position);

    // shift the banners to the left
    @Query("UPDATE HomePageBanner b SET b.position = b.position - 1 WHERE b.position >= :position")
    @Modifying
    void shiftBannersToLeft(@Param("position") Integer position);
    
    boolean existsByPosition(Integer position);
}