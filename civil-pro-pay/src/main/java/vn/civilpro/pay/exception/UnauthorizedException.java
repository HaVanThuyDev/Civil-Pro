package vn.civilpro.pay.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends PaymentException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
