package vn.civilpro.congdan.exception;

import vn.civilpro.congdan.model.enums.ErrorCode;
import vn.civilpro.congdan.exception.BaseException;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(ErrorCode errorCode, Object detail) {
        super(errorCode, detail);
    }
}