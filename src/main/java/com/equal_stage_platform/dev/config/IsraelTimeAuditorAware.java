package com.equal_stage_platform.dev.config;

import com.equal_stage_platform.dev.util.TimeUtils;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public class IsraelTimeAuditorAware implements DateTimeProvider {

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(TimeUtils.nowInIsrael());
    }
}