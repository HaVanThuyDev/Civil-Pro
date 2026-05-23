package vn.civilpro.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int code;
    private final String message;
    private final T data;
    private final PageMeta page;

    @Builder.Default
    private final Instant timestamp = Instant.now();
    private final String traceId;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(201)
                .message("Created successfully")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> paged(T data, long totalElements, int totalPages,
                                           int currentPage, int pageSize) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message("Success")
                .data(data)
                .page(PageMeta.builder()
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .hasNext(currentPage < totalPages - 1)
                        .hasPrevious(currentPage > 0)
                        .build())
                .build();
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageMeta {
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;
    }
}
