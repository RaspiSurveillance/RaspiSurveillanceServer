package de.calltopower.raspisurveillance.impl.db.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.calltopower.raspisurveillance.impl.model.RSUserVerificationTokenModel;

/**
 * User activation token model repository
 */
@Repository
public interface RSUserVerificationTokensRepository extends JpaRepository<RSUserVerificationTokenModel, UUID> {

    Set<RSUserVerificationTokenModel> findAllByUserId(UUID id);

    @Override
    Optional<RSUserVerificationTokenModel> findById(UUID id);

}
