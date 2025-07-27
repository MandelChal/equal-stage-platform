package com.equal_stage_platform.dev.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtils {
    public static LocalDateTime nowInIsrael() {
        return ZonedDateTime.now(ZoneId.of("Asia/Jerusalem")).toLocalDateTime();
    }
}
