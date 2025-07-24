package com.equal_stage_platform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pagination request parameters")
public class PaginationRequest {
    @Schema(description = "Page number (0-based)", example = "0")
    private int pageNum;

    @Schema(description = "Number of items per page", example = "10")
    private int pageSize;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
} 