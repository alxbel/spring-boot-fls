package com.github.alxbel.fls.store.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents application table.
 */
@Entity
@Table(name = "APPLICATION")
public class Application {

    @Id
    @Column(name = "APPLICATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    @ManyToOne
    @JoinColumn(name = "CONTACT_ID_FK")
    @JsonIgnore
    private Contact contact;

    @Column(name = "DT_CREATED")
    @JsonProperty(value = "dtCreated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dtCreated;

    @Column(name = "PRODUCT_NAME")
    @JsonProperty(value = "productName")
    private String productName;

    private Application() {} // JPA

    public Application(Contact contact, LocalDateTime dtCreated, String productName) {
        this.contact = contact;
        this.dtCreated = dtCreated == null ? LocalDateTime.now() : dtCreated;
        this.productName = productName;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public LocalDateTime getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(LocalDateTime dtCreated) {
        this.dtCreated = dtCreated;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
