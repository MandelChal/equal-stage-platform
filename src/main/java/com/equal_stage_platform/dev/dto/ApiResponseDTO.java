// package com.equal_stage_platform.dev.dto;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class ApiResponseDTO<T> {
//     private boolean success;
//     private String message;
//     private T data;
//     private String errorCode;
    
//     public static <T> ApiResponseDTO<T> success(T data) {
//         return ApiResponseDTO.<T>builder()
//                 .success(true)
//                 .data(data)
//                 .build();
//     }
    
//     public static <T> ApiResponseDTO<T> success(T data, String message) {
//         return ApiResponseDTO.<T>builder()
//                 .success(true)
//                 .message(message)
//                 .data(data)
//                 .build();
//     }
    
//     public static <T> ApiResponseDTO<T> error(String message) {
//         return ApiResponseDTO.<T>builder()
//                 .success(false)
//                 .message(message)
//                 .build();
//     }
    
//     public static <T> ApiResponseDTO<T> error(String message, String errorCode) {
//         return ApiResponseDTO.<T>builder()
//                 .success(false)
//                 .message(message)
//                 .errorCode(errorCode)
//                 .build();
//     }
// }
