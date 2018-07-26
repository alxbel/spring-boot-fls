package com.github.alxbel.fls.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown in case when contact was not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(Integer contactId) {
        super("Could not find contact with contact_id = " + contactId);
    }
}
