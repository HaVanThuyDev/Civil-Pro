package vn.civilpro.congdan.exception;

import lombok.Getter;
import vn.civilpro.congdan.model.enums.ErrorCode;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + (detail != null ? ": " + detail : ""));
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, Object detail) {
        this(errorCode, detail != null ? String.valueOf(detail) : null);
    }
}
