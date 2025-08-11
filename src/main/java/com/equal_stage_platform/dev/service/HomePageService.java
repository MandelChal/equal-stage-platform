package com.equal_stage_platform.dev.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.dto.AddHomePageBanner;
import com.equal_stage_platform.dev.dto.UpdateHomePageBannerDTO;
import com.equal_stage_platform.dev.model.HomePageBanner;
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
    public List<HomePageBanner> getAllBannerUrls() {
        return homePageRepository.findAllByOrderByPositionAsc();
    }

    @Transactional
    public HomePageBanner addBannerUrl(AddHomePageBanner addBannerObject) {
        if(homePageRepository.findByUrl(addBannerObject.getUrl()).isPresent()) {
            throw new HomePageExeption("Banner already exists with url: " + addBannerObject.getUrl());
        }
        int position = handleDisplayOrderForNewBanner(addBannerObject.getPosition());
        HomePageBanner banner = HomePageBanner.builder()
            .url(addBannerObject.getUrl())
            .title(addBannerObject.getTitle())
            .mediaType(addBannerObject.getMediaType())
            .position(position)
            .build();
        return homePageRepository.save(banner);
    }

    private int handleDisplayOrderForNewBanner(Integer position) {
        int maxDisplayOrder = homePageRepository.findMaxPosition();
        int pos = position == null || position > maxDisplayOrder ? maxDisplayOrder+1 : position;
        if(pos <= maxDisplayOrder) {
            homePageRepository.shiftBannersToRight(pos);
        }
        return pos;
    }

    @Transactional
    public String deleteBannerUrl(Integer id) {
        HomePageBanner banner = homePageRepository.findById(id)
            .orElseThrow(() -> new HomePageExeption("Banner not found with id: " + id));
        homePageRepository.shiftBannersToLeft(banner.getPosition());
        homePageRepository.delete(banner);
        return "Banner deleted successfully";
    }

    @Transactional
    public List<HomePageBanner> editBannerUrls(List<UpdateHomePageBannerDTO> updateList) {
        List<HomePageBanner> updatedBanners = new ArrayList<>();
        for (UpdateHomePageBannerDTO updateDTO : updateList) {
            updatedBanners.add(editSingleBanner(updateDTO));
        }
        return updatedBanners;
    }

    private HomePageBanner editSingleBanner(UpdateHomePageBannerDTO updateDTO) {
        HomePageBanner banner = homePageRepository.findById(updateDTO.getId())
            .orElseThrow(() -> new HomePageExeption("Banner not found with id: " + updateDTO.getId()));
        if(updateDTO.getUrl() != null) {
            if(homePageRepository.findByUrl(updateDTO.getUrl()).isPresent()) {
                throw new HomePageExeption("Banner already exists with url: " + updateDTO.getUrl());
            }
            banner.setUrl(updateDTO.getUrl());
        }
        if(updateDTO.getTitle() != null) {
            banner.setTitle(updateDTO.getTitle());
        }
        if(updateDTO.getMediaType() != null) {
            banner.setMediaType(updateDTO.getMediaType());
        }
        return homePageRepository.save(banner);
    }

    @Transactional
    public String reOrderBanners(List<Integer> newPositions) {
        // newPositions will look like [5,2,3,1,4] means the banner with id 5 should be the first, 2 should be the second, etc.
        if(new HashSet<>(newPositions).size()!= newPositions.size()) {
            throw new HomePageExeption("Duplicate positions are not allowed");
        }
        Map<Integer, HomePageBanner> bannerMap = new HashMap<>();
        for (HomePageBanner banner : homePageRepository.findAll()) {
            bannerMap.put(banner.getId(), banner);
        }
        for (int i = 0; i < newPositions.size(); i++) {
            Integer id = newPositions.get(i);
            HomePageBanner banner = bannerMap.get(id);
            if(banner == null) {
                throw new HomePageExeption("Banner not found with id: " + id);
            }
            banner.setPosition(i+1);
        }
        homePageRepository.saveAll(bannerMap.values());
        return "Banners reordered successfully";
    }
}