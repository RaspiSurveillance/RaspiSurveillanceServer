package de.calltopower.raspisurveillance.impl.db.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import de.calltopower.raspisurveillance.impl.enums.RSUserRole;
import de.calltopower.raspisurveillance.impl.model.RSRoleModel;
import jakarta.persistence.QueryHint;

/**
 * Role model repository
 */
@Repository
public interface RSUserRoleRepository extends JpaRepository<RSRoleModel, UUID> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    Optional<RSRoleModel> findByName(RSUserRole name);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    <S extends RSRoleModel> S saveAndFlush(S entity);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    List<RSRoleModel> findAll();

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    @Override
    Optional<RSRoleModel> findById(UUID id);

}
