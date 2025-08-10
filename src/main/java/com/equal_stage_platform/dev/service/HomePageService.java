package com.equal_stage_platform.dev.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.equal_stage_platform.dev.dto.AddHomePageBanner;
import com.equal_stage_platform.dev.dto.UpdateHomePageBannerDTO;
import com.equal_stage_platform.dev.model.HomePageBanner;
import com.equal_stage_platform.dev.model.enums.BannerObjectType;
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
        return homePageRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional
    public String addBannerUrl(String url, String title, BannerObjectType objectType, Integer displayOrder) {
        // Validate URL uniqueness
        if (homePageRepository.findByUrl(url).isPresent()) {
            throw new HomePageExeption("Banner with this URL already exists");
        }

        // Handle display order
        Integer finalDisplayOrder = handleDisplayOrderForNewBanner(displayOrder);
        
        HomePageBanner banner = HomePageBanner.builder()
                .url(url)
                .title(title)
                .objectType(objectType)
                .displayOrder(finalDisplayOrder)
                .build();
        
        homePageRepository.save(banner);
        return "Banner added successfully with display order: " + finalDisplayOrder;
    }

    @Transactional
    public String addBannerUrl(AddHomePageBanner addBannerObject) {
        return addBannerUrl(
            addBannerObject.getUrl(),
            addBannerObject.getTitle(),
            addBannerObject.getObjectType(),
            addBannerObject.getDisplayOrder()
        );
    }

    @Transactional
    public String deleteBannerUrl(Long id) {
        Optional<HomePageBanner> bannerOpt = homePageRepository.findById(id);
        if (bannerOpt.isEmpty()) {
            throw new HomePageExeption("Banner with ID not found: " + id);
        }
        
        HomePageBanner banner = bannerOpt.get();
        Integer deletedDisplayOrder = banner.getDisplayOrder();
        
        homePageRepository.delete(banner);
        
        // Reorder remaining banners to maintain continuity
        reorderBannersAfterDeletion(deletedDisplayOrder);
        
        return "Banner deleted successfully";
    }

    @Transactional
    public String editBannerUrls(List<UpdateHomePageBannerDTO> updateList) {
        for (UpdateHomePageBannerDTO updateDTO : updateList) {
            editSingleBanner(updateDTO);
        }
        return "Banners updated successfully";
    }
    
    /*
     * private methods to handle the banner operations (add, delete, edit)
     * 
    */
    private void editSingleBanner(UpdateHomePageBannerDTO updateDTO) {
        Optional<HomePageBanner> bannerOpt = homePageRepository.findById(updateDTO.getId());
        if (bannerOpt.isEmpty()) {
            throw new HomePageExeption("Banner not found with ID: " + updateDTO.getId());
        }
        
        HomePageBanner banner = bannerOpt.get();
        Integer oldDisplayOrder = banner.getDisplayOrder();
        
        // Update fields if provided
        if (updateDTO.getUrl() != null) {
            // Check URL uniqueness only if URL is being changed
            if (!banner.getUrl().equals(updateDTO.getUrl()) && 
                homePageRepository.findByUrl(updateDTO.getUrl()).isPresent()) {
                throw new HomePageExeption("Banner with this URL already exists");
            }
            banner.setUrl(updateDTO.getUrl());
        }
        
        if (updateDTO.getTitle() != null) {
            banner.setTitle(updateDTO.getTitle());
        }
        
        if (updateDTO.getObjectType() != null) {
            banner.setObjectType(updateDTO.getObjectType());
        }
        
        // Handle display order change
        if (updateDTO.getDisplayOrder() != null && !oldDisplayOrder.equals(updateDTO.getDisplayOrder())) {
            handleDisplayOrderChange(banner, oldDisplayOrder, updateDTO.getDisplayOrder());
        }
        try {
            homePageRepository.save(banner);
        } catch (DataIntegrityViolationException e) {
            throw new HomePageExeption("Failed to update banner due to url conflict");
        }
    }
    
    private Integer handleDisplayOrderForNewBanner(Integer requestedDisplayOrder) {
        Integer maxDisplayOrder = homePageRepository.findMaxDisplayOrder();
        
        if (requestedDisplayOrder == null) {
            // If no display order specified, append to the end
            return maxDisplayOrder + 1;
        }
        
        if (requestedDisplayOrder <= 0) {
            throw new HomePageExeption("Display order must be greater than 0");
        }
        
        if (requestedDisplayOrder > maxDisplayOrder + 1) {
            // If requested order is beyond max, place at the end
            return maxDisplayOrder + 1;
        }
        
        // Shift existing banners with display order >= requestedDisplayOrder
        List<HomePageBanner> bannersToShift = homePageRepository.findAllWithDisplayOrderGreaterThan(requestedDisplayOrder - 1);
        for(int i = bannersToShift.size() - 1; i >= 0; i--) {
            HomePageBanner banner = bannersToShift.get(i);
            banner.setDisplayOrder(banner.getDisplayOrder() + 1);
            homePageRepository.saveAndFlush(banner);
        }

        return requestedDisplayOrder;
    }
    
    private void reorderBannersAfterDeletion(Integer deletedDisplayOrder) {
        List<HomePageBanner> bannersToReorder = homePageRepository.findAllWithDisplayOrderGreaterThan(deletedDisplayOrder);
        
        for (HomePageBanner banner : bannersToReorder) {
            banner.setDisplayOrder(banner.getDisplayOrder() - 1);
            homePageRepository.save(banner);
        }
    }
    
    private void handleDisplayOrderChange(HomePageBanner banner, Integer oldDisplayOrder, Integer newDisplayOrder) {
        Integer maxDisplayOrder = homePageRepository.findMaxDisplayOrder();
        
        if (newDisplayOrder <= 0) {
            throw new HomePageExeption("Display order must be greater than 0");
        }
        
        if (newDisplayOrder > maxDisplayOrder) {
            newDisplayOrder = maxDisplayOrder;
        }
        
        if (oldDisplayOrder.equals(newDisplayOrder)) {
            return; // No change needed
        }
        
        if (newDisplayOrder < oldDisplayOrder) {
            // shift banners to the right
            List<HomePageBanner> bannersToShift = homePageRepository.findAllByDisplayOrderBetween(newDisplayOrder, oldDisplayOrder - 1);
            for(int i = bannersToShift.size() - 1; i >= 0; i--) {
                HomePageBanner b = bannersToShift.get(i);
                b.setDisplayOrder(b.getDisplayOrder() + 1);
                homePageRepository.saveAndFlush(b);
            }
        } else {
            banner.setDisplayOrder(-1);
            homePageRepository.saveAndFlush(banner);
            // shift banners to the left
            List<HomePageBanner> bannersToShift = homePageRepository.findAllByDisplayOrderBetween(oldDisplayOrder + 1, newDisplayOrder);
            for (HomePageBanner b : bannersToShift) {
                b.setDisplayOrder(b.getDisplayOrder() - 1);
                homePageRepository.saveAndFlush(b);
            }
        }
        
        banner.setDisplayOrder(newDisplayOrder);
        homePageRepository.saveAndFlush(banner);
    }
}
