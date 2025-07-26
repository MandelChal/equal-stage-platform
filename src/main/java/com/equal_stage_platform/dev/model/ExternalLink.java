package com.equal_stage_platform.dev.model;

import com.equal_stage_platform.dev.dto.ExternalLinkDTO;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ExternalLink {
    private String url;
    private String description;

    public ExternalLink(ExternalLinkDTO externalLinkDTO) {
        this.url = externalLinkDTO.getUrl();
        this.description = externalLinkDTO.getDescription();
    }
}
