package de.calltopower.raspisurveillance.impl.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.calltopower.raspisurveillance.api.model.RSModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model implementation for users
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cache(region = "users", usage = CacheConcurrencyStrategy.READ_WRITE)
@EqualsAndHashCode(callSuper = false, exclude = { "roles" })
// @formatter:off
@Table(name = RSUserModel.TABLE_NAME, uniqueConstraints = {
        @UniqueConstraint(columnNames = "STR_USERNAME"),
        @UniqueConstraint(columnNames = "STR_EMAIL")
    }
)
// @formatter:on
public class RSUserModel implements Serializable, RSModel {

    private static final long serialVersionUID = 4806970452036198396L;

    /**
     * The table name
     */
    public static final String TABLE_NAME = "users";

    /**
     * The role join table name
     */
    public static final String TABLE_NAME_JOIN_ROLE = "user_roles";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "NR_ID", columnDefinition = "BINARY(16)")
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "STR_USERNAME")
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email
    @Column(name = "STR_EMAIL")
    private String email;

    @NotBlank
    @Size(max = 100)
    @Column(name = "STR_PASSWORD")
    private String password;

    @NotBlank
    @Column(name = "JSON_DATA")
    private String jsonData;

    @Column(name = "STATUS_VERIFIED")
    private boolean statusVerified;

    @Builder.Default
    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = RSUserModel.TABLE_NAME_JOIN_ROLE, joinColumns = @JoinColumn(name = "NR_USER_ID"), inverseJoinColumns = @JoinColumn(name = "NR_ROLE_ID"))
    private Set<RSRoleModel> roles = new HashSet<>();

    @Override
    public String toString() {
        // @formatter:off
        return String.format(
                "RSUserModel["
                    + "id='%s',"
                    + "username='%s',"
                    + "email='%s'"
                + "]",
                id,
                username,
                email
               );
        // @formatter:on
    }

}
