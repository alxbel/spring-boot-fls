package com.github.alxbel.fls.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown in case when application was not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Integer applicationId, Integer contactId) {
        super(String.format("Could not find application with application_id = %d and contact_id_fk = %d",
                applicationId, contactId));
    }
}
