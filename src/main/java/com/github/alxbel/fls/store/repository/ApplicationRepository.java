package com.github.alxbel.fls.store.repository;

import com.github.alxbel.fls.store.entity.Application;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for manipulating data in application table.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    Collection<Application> findAllByContactContactId(Integer contactId);

    Optional<Application> findByContactContactIdAndApplicationId(Integer contactId, Integer applicationId);

    @Query("select a from Application a where dtCreated = (select max(dtCreated) from Application)")
    List<Application> findLatest(Pageable pageable);
}
