package com.equal_stage_platform.dev.repository;
import com.equal_stage_platform.dev.model.HomePageBannerObject;
import com.equal_stage_platform.dev.model.enums.bannerObjectType;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
// import java.util.Optional;
public interface HomePageRepository extends JpaRepository<HomePageBannerObject, String> {
    List<HomePageBannerObject> findByObjectType(bannerObjectType objectType);
}