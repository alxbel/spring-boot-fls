package com.github.alxbel.fls.store.controller;

import com.github.alxbel.fls.store.entity.Application;
import com.github.alxbel.fls.store.exception.ApplicationNotFoundException;
import com.github.alxbel.fls.store.exception.ContactNotFoundException;
import com.github.alxbel.fls.store.repository.ApplicationRepository;
import com.github.alxbel.fls.store.repository.ContactRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "store", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * Get list of applications by contact id.
     *
     * @param   contactId contact id.
     *
     * @return  collection of applications.
     */
    @ApiOperation(notes = "Get list of applications by contact id", value = "Get all contact's applications",
            nickname = "getApplicationsByContactId",
            tags = {"Applications"} )
    @GetMapping(path = "/applications/{contactId}")
    public Collection<Application> getApplicationsByContactId(@PathVariable Integer contactId) {
        validateContact(contactId);

        return applicationRepository.findAllByContactContactId(contactId);
    }

    /**
     * Get application with latest creation date.
     *
     * @return application.
     */
    @ApiOperation(notes = "Get application with the newest creation date", value = "Get latest application",
            nickname = "getLatestApplication",
            tags = {"Application"} )
    @GetMapping(path = "/applications/latest")
    public Application getLatestApplication() {
        return applicationRepository.findLatest(PageRequest.of(0, 1)).get(0);
    }

    /**
     * Creates new application for contact.
     *
     * @param   contactId     contact id.
     * @param   application   application request body.
     * @return  created application.
     */
    @ApiOperation(notes = "Add new application for the contact", value = "Add new application",
            nickname = "addApplication",
            tags = {"Application"} )
    @PostMapping(path = "/applications/add/{contactId}")
    public ResponseEntity<?> addApplication(@PathVariable Integer contactId, @RequestBody Application application) {
        return contactRepository
                .findByContactId(contactId)
                .map(contact -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(applicationRepository
                                .save(new Application(contact, application.getDtCreated(), application.getProductName()))))
                .orElseThrow(() -> new ContactNotFoundException(contactId));
    }

    /**
     * Update application by contactId and applicationId.
     *
     * @param contactId         application's contact id.
     * @param applicationId     application id.
     * @param updApp            new values.
     * @return                  updated application.
     */
    @ApiOperation(notes = "Update contact's application", value = "Update application",
            nickname = "updateApplication",
            tags = {"Application"} )
    @PutMapping(path = "/applications/update/{contactId}/{applicationId}", headers = "Accept=application/json")
    public ResponseEntity<?> updateApplication(@PathVariable Integer contactId, @PathVariable Integer applicationId, @RequestBody Application updApp) {
        return applicationRepository
                .findByContactContactIdAndApplicationId(contactId, applicationId)
                .map(existingApp -> {
                    existingApp.setProductName(updApp.getProductName());
                    existingApp.setDtCreated(updApp.getDtCreated());
                    ResponseEntity rs = ResponseEntity
                            .ok(applicationRepository.save(existingApp));
                    return rs;
                })
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId, contactId));
    }

    @PutMapping(path= "/foo")
    public Application foo(@RequestBody Application application) {
        return application;
    }

    /**
     * Verify that contact exists.
     *
     * @param contactId contact id.
     */
    private void validateContact(Integer contactId) {
        contactRepository
                .findByContactId(contactId)
                .orElseThrow(() -> new ContactNotFoundException(contactId));
    }
}
