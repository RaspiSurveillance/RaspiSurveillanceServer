package de.calltopower.raspisurveillance.impl.db.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.calltopower.raspisurveillance.impl.model.RSUserForgotPasswordTokenModel;

/**
 * User forgot password token model repository
 */
@Repository
public interface RSUserForgotPasswordTokensRepository extends JpaRepository<RSUserForgotPasswordTokenModel, UUID> {

    Set<RSUserForgotPasswordTokenModel> findAllByUserId(UUID id);

    @Override
    Optional<RSUserForgotPasswordTokenModel> findById(UUID id);

}
