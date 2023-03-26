package de.calltopower.raspisurveillance.impl.db.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import de.calltopower.raspisurveillance.impl.model.RSUserModel;
import jakarta.persistence.QueryHint;

/**
 * User model repository
 */
@Repository
public interface RSUserRepository extends JpaRepository<RSUserModel, UUID> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    Optional<RSUserModel> findByUsername(String username);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    Boolean existsByUsername(String username);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    Boolean existsByEmail(String email);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    List<RSUserModel> findAll();

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    <S extends RSUserModel> S saveAndFlush(S entity);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    Optional<RSUserModel> findById(UUID id);

}
