package vn.civilpro.household.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(404, String.format("%s not found with identifier: %s", resourceName, identifier));
    }
}
