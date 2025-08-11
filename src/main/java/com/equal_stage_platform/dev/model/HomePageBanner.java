package com.equal_stage_platform.dev.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import com.equal_stage_platform.dev.model.enums.MediaType;

@Data
@Entity
@Table(name = "5$^)_home_page_banner")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class HomePageBanner extends BaseAuditableEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "url", nullable = false, length = 2048, unique = true)
    private String url;

    @Column(name = "title", length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Column(name = "position", nullable = false)
    private Integer position;
}