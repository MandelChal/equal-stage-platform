package com.equal_stage_platform.dev.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.dto.AddHomePageBanner;
import com.equal_stage_platform.dev.dto.HomePageBannerDTO;
import com.equal_stage_platform.dev.model.HomePageBannerObject;
import com.equal_stage_platform.dev.model.enums.bannerObjectType;
import com.equal_stage_platform.dev.repository.HomePageRepository;
import com.equal_stage_platform.dev.exception.HomePageExeption;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HomePageService {
    private final HomePageRepository homePageRepository;
    public HomePageService(HomePageRepository homePageRepository) {
        this.homePageRepository = homePageRepository;
    }

    @Transactional(readOnly = true)
    public HomePageBannerDTO getAllBannerUrls() {
        List<HomePageBannerObject> imageUrls = homePageRepository.findByObjectType(bannerObjectType.PHOTO);
                // .orElseThrow(() -> new RuntimeException("No images found for banner"));
        List<HomePageBannerObject> videosUrls = homePageRepository.findByObjectType(bannerObjectType.VIDEO);
                // .orElseThrow(() -> new RuntimeException("No videos found for banner"));

        return new HomePageBannerDTO(imageUrls, videosUrls);
    }

    @Transactional
    public String addBannerUrl(String url, String title, bannerObjectType objectType, Integer orderIndex) {
        HomePageBannerObject bannerObject = new HomePageBannerObject(url, title, objectType, orderIndex);
        homePageRepository.save(bannerObject);
        return "Banner URL added successfully";
    }

    @Transactional
    public String addBannerUrl(AddHomePageBanner addBannerObject) {
        HomePageBannerObject bannerObject = new HomePageBannerObject(addBannerObject);
        homePageRepository.save(bannerObject);
        return "Banner URL added successfully";
    }

    @Transactional
    public String deleteBannerUrl(String url) {
        if (url == null || url.isEmpty() || !homePageRepository.existsById(url)) {
            throw new HomePageExeption("Banner URL not found");
        }
        homePageRepository.deleteById(url);
        return "Banner URL deleted successfully";
    }

    //TODO - support editing
    
}
