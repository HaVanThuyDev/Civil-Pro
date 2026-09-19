package vn.civilpro.congdan.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "DM_CITIZEN",
        indexes = {
                @Index(name = "IDX_FULL_NAME", columnList = "FULL_NAME"),
                @Index(name = "IDX_FULL_NAME_ASCII", columnList = "FULL_NAME_ASCII"),
                @Index(name = "IDX_DATE_OF_BIRTH", columnList = "DATE_OF_BIRTH"),
                @Index(name = "IDX_ID_CARD_EXPIRY_DATE", columnList = "ID_CARD_EXPIRY_DATE"),
                @Index(name = "IDX_PERMANENT_AREA_CODE", columnList = "PERMANENT_AREA_CODE"),
                @Index(name = "IDX_CITIZEN_TYPE", columnList = "CITIZEN_TYPE"),
                @Index(name = "IDX_HOUSEHOLD_ID", columnList = "HOUSEHOLD_ID"),
                @Index(name = "IDX_STATUS", columnList = "STATUS")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Citizen extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CITIZEN_CODE", nullable = false, unique = true, length = 20)
    private String citizenCode;

    @Column(name = "FULL_NAME", nullable = false, length = 255)
    private String fullName;

    @Column(name = "FULL_NAME_ASCII", length = 255)
    private String fullNameAscii;

    @Column(name = "GENDER", nullable = false)
    private Integer gender;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "PLACE_OF_BIRTH", length = 500)
    private String placeOfBirth;

    @Column(name = "ETHNICITY", length = 50)
    private String ethnicity;

    @Column(name = "RELIGION", length = 50)
    private String religion;

    @Column(name = "NATIONALITY", length = 50)
    @Builder.Default
    private String nationality = "VIỆT NAM";

    // ---- Identification Documents ----
    @Convert(converter = vn.civilpro.congdan.security.Aes256EncryptConverter.class)
    @Column(name = "ID_CARD_NUMBER", unique = true, length = 100)
    private String idCardNumber;

    @Column(name = "ID_CARD_ISSUED_DATE")
    private LocalDate idCardIssuedDate;

    @Column(name = "ID_CARD_ISSUED_PLACE", length = 255)
    private String idCardIssuedPlace;

    @Column(name = "ID_CARD_EXPIRY_DATE")
    private LocalDate idCardExpiryDate;

    @Column(name = "PASSPORT_NUMBER", length = 20)
    private String passportNumber;

    @Column(name = "PASSPORT_EXPIRY_DATE")
    private LocalDate passportExpiryDate;

    // ---- Contact Details ----
    @Convert(converter = vn.civilpro.congdan.security.Aes256EncryptConverter.class)
    @Column(name = "PHONE_NUMBER", length = 100)
    private String phoneNumber;

    @Column(name = "EMAIL", length = 255)
    private String email;

    // ---- Residency ----
    @Column(name = "PERMANENT_AREA_CODE", length = 20)
    private String permanentAreaCode;

    @Column(name = "PERMANENT_ADDRESS", length = 500)
    private String permanentAddress;

    @Column(name = "TEMPORARY_AREA_CODE", length = 20)
    private String temporaryAreaCode;

    @Column(name = "TEMPORARY_ADDRESS", length = 500)
    private String temporaryAddress;

    // ---- Occupation & Education ----
    @Column(name = "OCCUPATION", length = 100)
    private String occupation;

    @Column(name = "EDUCATION_LEVEL", length = 50)
    private String educationLevel;

    @Column(name = "WORKPLACE", length = 255)
    private String workplace;

    // ---- Classification & Household ----
    @Column(name = "CITIZEN_TYPE", length = 50)
    private String citizenType;

    @Column(name = "IS_HOUSEHOLD_HEAD")
    @Builder.Default
    private Integer isHouseholdHead = 0; // Configured as tinyint matching DB

    @Column(name = "HOUSEHOLD_ID")
    private Long householdId;

    // ---- Status ----
    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private Integer status = 1; // 1=Active, 0=Inactive/Deceased (mapped to tinyint)

    @Column(name = "DEATH_DATE")
    private LocalDate deathDate;

    @Column(name = "STATUS_REASON", length = 500)
    private String statusReason;

    @Version
    @Column(name = "VERSION", nullable = false)
    @Builder.Default
    private Integer version = 0;
}