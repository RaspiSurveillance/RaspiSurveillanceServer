package de.calltopower.raspisurveillance.impl.db.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import de.calltopower.raspisurveillance.impl.model.RSServerModel;
import jakarta.persistence.QueryHint;

/**
 * Server model repository
 */
@Repository
public interface RSServerRepository extends JpaRepository<RSServerModel, UUID> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    List<RSServerModel> findAll();

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    <S extends RSServerModel> S saveAndFlush(S entity);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    Optional<RSServerModel> findById(UUID id);

}
