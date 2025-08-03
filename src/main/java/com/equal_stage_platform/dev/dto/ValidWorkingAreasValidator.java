package com.equal_stage_platform.dev.dto;

import com.equal_stage_platform.dev.model.enums.Area;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidWorkingAreasValidator implements ConstraintValidator<ValidWorkingAreas, Set<Area>> {
    
    @Override
    public boolean isValid(Set<Area> workingAreas, ConstraintValidatorContext context) {
        // Null values should be handled by @NotNull annotation
        if (workingAreas == null) {
            return true;
        }
        
        // Check that all areas are valid enum values (not null)
        for (Area area : workingAreas) {
            if (area == null) {
                return false;
            }
        }
        
        // Business logic validation:
        // 1. Prevent empty sets (handled by @NotEmpty)
        if (workingAreas.isEmpty()) {
            return false;
        }
        
        // 2. Limit maximum number of working areas
        if (workingAreas.size() > 4) {
            return false;
        }
        
        // 3. Prevent mixing ONLINE_ONLY with physical areas
        boolean hasOnline = workingAreas.contains(Area.ONLINE_ONLY);
        boolean hasPhysical = workingAreas.stream()
            .anyMatch(area -> area == Area.CENTER || area == Area.NORTH || area == Area.SOUTH);
        
        if (hasOnline && hasPhysical) {
            return false;
        }
        
        return true;
    }
}