package vn.civilpro.congdan.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;

    // --- SUCCESS RESPONSES ---

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> paged(T data, long totalElements, int totalPages, int pageNumber, int pageSize) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return success(data);
    }


    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("Created successfully")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }

    // --- ERROR RESPONSES ---

    public static <T> ApiResponse<T> error(HttpStatus httpStatus, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(httpStatus.value())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }
}