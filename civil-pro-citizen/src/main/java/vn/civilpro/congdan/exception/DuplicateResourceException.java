package vn.civilpro.congdan.exception;

import vn.civilpro.congdan.model.enums.ErrorCode;
import vn.civilpro.congdan.exception.BaseException;

public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
