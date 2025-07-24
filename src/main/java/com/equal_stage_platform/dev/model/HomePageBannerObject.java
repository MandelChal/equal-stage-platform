package com.equal_stage_platform.dev.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.equal_stage_platform.dev.dto.AddHomePageBanner;
import com.equal_stage_platform.dev.model.enums.bannerObjectType;

@Data
@Entity
@Table(name = "5)l_home_page_banner")
@AllArgsConstructor
@NoArgsConstructor
public class HomePageBannerObject {
    @Id
    @Column(name = "URL", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "title", nullable = true)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false)
    private bannerObjectType objectType;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    public HomePageBannerObject(AddHomePageBanner bannerObject) {
        this.url = bannerObject.getUrl();
        this.title = bannerObject.getTitle();
        this.objectType = bannerObject.getObjectType();
        this.orderIndex = bannerObject.getOrderIndex();
    }
}
