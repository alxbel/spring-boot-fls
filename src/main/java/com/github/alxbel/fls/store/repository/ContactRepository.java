package com.github.alxbel.fls.store.repository;

import com.github.alxbel.fls.store.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for manipulating data in contact table.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Optional<Contact> findByContactId(Integer contactId);
}
