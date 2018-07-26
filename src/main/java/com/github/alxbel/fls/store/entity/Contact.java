package com.github.alxbel.fls.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents contact table.
 */
@Entity
@Table(name = "CONTACT")
public class Contact {

    @Id
    @Column(name = "CONTACT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactId;

    @OneToMany(mappedBy = "contact")
    @JsonIgnore
    private List<Application> applications = new ArrayList<>();

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
