package vn.civilpro.congdan.model.enums;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Citizen domain (CD = Cong Dan)
    CD_NOT_FOUND("CD_001", "Citizen not found", HttpStatus.NOT_FOUND),
    CD_CCCD_EXISTS("CD_002", "ID card number already exists", HttpStatus.CONFLICT),
    RESOURCE_NOT_FOUND("RESOURCE_001", "Resource not found", HttpStatus.NOT_FOUND),

    // Add more domain error codes here as needed, e.g.:
    // HK_NOT_FOUND("HK_001", "Household not found", HttpStatus.NOT_FOUND),

    INTERNAL_ERROR("SYS_000", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}