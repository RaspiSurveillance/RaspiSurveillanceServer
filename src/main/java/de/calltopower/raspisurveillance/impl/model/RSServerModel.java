package de.calltopower.raspisurveillance.impl.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import de.calltopower.raspisurveillance.api.model.RSModel;
import de.calltopower.raspisurveillance.impl.enums.RSServerStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model implementation for servers
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cache(region = "servers", usage = CacheConcurrencyStrategy.READ_WRITE)
@EqualsAndHashCode(callSuper = false, exclude = { "attributesCamerastream" })
@Table(name = RSServerModel.TABLE_NAME)
public class RSServerModel implements Serializable, RSModel {

    private static final long serialVersionUID = 2057035435477510762L;

    /**
     * The table name
     */
    public static final String TABLE_NAME = "servers";

    /**
     * The table name for the attributes of camerastream
     */
    public static final String TABLE_NAME_ATTRIBUTES_CAMERASTREAM = "attributes_camerastream";

    /**
     * The table name for the attributes of surveillance
     */
    public static final String TABLE_NAME_ATTRIBUTES_SURVEILLANCE = "attributes_surveillance";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "NR_ID", columnDefinition = "BINARY(16)")
    private UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED")
    private Date createdDate;

    @NotBlank
    @Size(max = 100)
    @Column(name = "STR_NAME")
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(name = "STR_URL")
    private String url;

    @Size(max = 100)
    @Column(name = "STR_USERNAME")
    private String username;

    @Size(max = 100)
    @Column(name = "STR_PASSWORD")
    private String password;

    @Column(name = "IS_MASTER")
    private boolean isMaster;

    @Column(name = "HAS_SERVICE_CAMERASTREAM")
    private boolean hasServiceCamerastream;

    @Column(name = "HAS_SERVICE_SURVEILLANCE")
    private boolean hasServiceSurveillance;

    @Size(max = 100)
    @Column(name = "STR_URL_MASTER")
    private String urlMaster;

    @Size(max = 100)
    @Column(name = "STR_ID_MASTER")
    private String idMaster;

    @Size(max = 100)
    @Column(name = "STR_USERNAME_MASTER")
    private String usernameMaster;

    @Size(max = 100)
    @Column(name = "STR_PASSWORD_MASTER")
    private String passwordMaster;

    @Size(max = 100)
    @Column(name = "STR_URL_CAMERASTREAM")
    private String urlCamerastream;

    @Size(max = 100)
    @Column(name = "STR_USERNAME_CAMERASTREAM")
    private String usernameCamerastream;

    @Size(max = 100)
    @Column(name = "STR_PASSWORD_CAMERASTREAM")
    private String passwordCamerastream;

    @ElementCollection
    @MapKeyColumn(name = "STR_KEY")
    @Column(name = "STR_VALUE")
    @CollectionTable(name = TABLE_NAME_ATTRIBUTES_CAMERASTREAM, joinColumns = { @JoinColumn(name = "NR_SERVER_ID") })
    private Map<String, String> attributesCamerastream;

    @ElementCollection
    @MapKeyColumn(name = "STR_KEY")
    @Column(name = "STR_VALUE")
    @CollectionTable(name = TABLE_NAME_ATTRIBUTES_SURVEILLANCE, joinColumns = { @JoinColumn(name = "NR_SERVER_ID") })
    private Map<String, String> attributesSurveillance;

    @NotNull
    @Column(name = "STR_STATUS")
    @Enumerated(EnumType.STRING)
    private RSServerStatus status;

    @NotBlank
    @Column(name = "JSON_DATA")
    private String jsonData;

    @Override
    public String toString() {
        // @formatter:off
        return String.format(
                "RSServerModel["
                    + "id='%s',"
                    + "createdDate='%s',"
                    + "name='%s',"
                    + "url='%s',"
                    + "username='%s',"
                    + "isMaster='%b',"
                    + "hasServiceCamerastream='%b',"
                    + "hasServiceSurveillance='%b',"
                    + "urlMaster='%s',"
                    + "idMaster='%s',"
                    + "usernameMaster='%s',"
                    + "urlCamerastream='%s',"
                    + "usernameCamerastream='%s',"
                    + "status='%s'"
                + "]",
                id,
                createdDate,
                name,
                url,
                username,
                isMaster,
                hasServiceCamerastream,
                hasServiceSurveillance,
                urlMaster,
                idMaster, 
                usernameMaster,
                urlCamerastream,
                usernameCamerastream,
                status
               );
        // @formatter:on
    }

}
