package vn.civilpro.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus
public class EntityValidationException extends RuntimeException {
    private Object details ;
    public EntityValidationException(String message) {
        super(message);
    }
    public EntityValidationException(String message, Object details) {

        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}