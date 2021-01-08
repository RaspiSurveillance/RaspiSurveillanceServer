package de.calltopower.raspisurveillance.impl.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import de.calltopower.raspisurveillance.api.model.RSModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model implementation for user activation tokens
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = RSUserVerificationTokenModel.TABLE_NAME)
public class RSUserVerificationTokenModel implements Serializable, RSModel {

    private static final long serialVersionUID = 5885230262037608272L;

    /**
     * The table name
     */
    public static final String TABLE_NAME = "user_verification_tokens";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "NR_ID", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "NR_USER_ID", columnDefinition = "BINARY(16)")
    private UUID userId;

    @Override
    public String toString() {
        // @formatter:off
        return String.format(
                "RSUserActivationTokenModel["
                    + "id='%s',"
                    + "userId='%s'"
                + "]",
                id,
                userId
               );
        // @formatter:on
    }

}
