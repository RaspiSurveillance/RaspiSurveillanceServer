package de.calltopower.raspisurveillance.impl.model;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import de.calltopower.raspisurveillance.api.model.RSModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model implementation for forgot password tokens
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = RSUserForgotPasswordTokenModel.TABLE_NAME)
public class RSUserForgotPasswordTokenModel implements Serializable, RSModel {

    private static final long serialVersionUID = 2057035435477510762L;

    /**
     * The table name
     */
    public static final String TABLE_NAME = "user_forgot_password_tokens";

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
                "RSUserForgotPasswordTokenModel["
                    + "id='%s',"
                    + "userId='%s'"
                + "]",
                id,
                userId
               );
        // @formatter:on
    }

}
