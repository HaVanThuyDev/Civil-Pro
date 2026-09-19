package vn.civilpro.pay.exception;

import org.springframework.http.HttpStatus;

public class DuplicateTransactionException extends PaymentException {

    public DuplicateTransactionException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
