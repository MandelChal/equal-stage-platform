package com.equal_stage_platform.dev.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equal_stage_platform.dev.dto.CreateTargetAudienceDTO;
import com.equal_stage_platform.dev.dto.UpdateTargetAudienceDTO;
import com.equal_stage_platform.dev.model.TargetAudience;
import com.equal_stage_platform.dev.repository.TargetAudienceRepository;
import com.equal_stage_platform.dev.exception.TargetAudienceException;

import java.util.List;

@Service
public class TargetAudienceService {
    private final TargetAudienceRepository targetAudienceRepository;

    public TargetAudienceService(TargetAudienceRepository targetAudienceRepository) {
        this.targetAudienceRepository = targetAudienceRepository;
    }

    @Transactional(readOnly = true)
    public List<TargetAudience> getAllTargetAudiences() {
        return targetAudienceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TargetAudience getTargetAudienceById(Long id) {
        return targetAudienceRepository.findById(id)
                .orElseThrow(() -> new TargetAudienceException("TargetAudience not found with ID: " + id));
    }

    @Transactional
    public TargetAudience createTargetAudience(CreateTargetAudienceDTO targetAudience) {
        try{
            TargetAudience newTargetAudience = new TargetAudience(targetAudience);
            return targetAudienceRepository.save(newTargetAudience);
        } catch (DataIntegrityViolationException e) {
            throw new TargetAudienceException("TargetAudience with type '" + targetAudience.getType() + "' already exists", e);
        }
    }

    @Transactional
    public TargetAudience updateTargetAudience(Long id, UpdateTargetAudienceDTO targetAudienceDetails) {
        TargetAudience targetAudience = targetAudienceRepository.findById(id)
                .orElseThrow(() -> new TargetAudienceException("TargetAudience not found with ID: " + id));
        
        if (targetAudienceDetails.getType() != null)
            targetAudience.setType(targetAudienceDetails.getType());
        if (targetAudienceDetails.getDescription() != null)
            targetAudience.setDescription(targetAudienceDetails.getDescription());
        
        return targetAudienceRepository.save(targetAudience);
    }

    @Transactional
    public void deleteTargetAudience(Long id) {
        TargetAudience targetAudience = targetAudienceRepository.findById(id)
                .orElseThrow(() -> new TargetAudienceException("TargetAudience not found with ID: " + id));
        targetAudienceRepository.delete(targetAudience);
    }

    @Transactional(readOnly = true)
    public List<TargetAudience> findByPrefixName(String prefix){
        return targetAudienceRepository.findByTypeStartingWithIgnoreCase(prefix);
    }
}