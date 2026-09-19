package vn.civilpro.pay.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PaymentException extends RuntimeException {

    private final HttpStatus status;

    public PaymentException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public PaymentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
