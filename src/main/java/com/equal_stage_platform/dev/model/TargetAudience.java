package com.equal_stage_platform.dev.model;

import com.equal_stage_platform.dev.dto.CreateTargetAudienceDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "#48e$_target_audiences")
public class TargetAudience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_audience_id", nullable = false, unique = true)
    private Long targetAudienceId;

    @Column(name = "type", nullable = false, unique = true)
    private String type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;    

    public TargetAudience(CreateTargetAudienceDTO targetAudienceDTO) {
        this.type = targetAudienceDTO.getType();
        this.description = targetAudienceDTO.getDescription();
    }
}
