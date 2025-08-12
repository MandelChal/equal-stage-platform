package com.equal_stage_platform.dev.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import java.util.Set;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.FetchType;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "about_us")
public class AboutUs extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "about_us_image_url")
    @Column(name = "image_url")
    private Set<String> imageUrls;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "about_us_video_url")
    @Column(name = "video_url")
    private Set<String> videoUrls;
}