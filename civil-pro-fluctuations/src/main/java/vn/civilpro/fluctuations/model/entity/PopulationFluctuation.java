package vn.civilpro.fluctuations.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "POPULATION_FLUCTUATION", indexes = {
        @Index(name = "IDX_PF_TYPE", columnList = "FLUCTUATION_TYPE"),
        @Index(name = "IDX_PF_AREA", columnList = "AREA_CODE"),
        @Index(name = "IDX_PF_YEAR_MONTH", columnList = "YEAR, MONTH"),
        @Index(name = "IDX_PF_DATE", columnList = "FLUCTUATION_DATE")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopulationFluctuation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FLUCTUATION_CODE", nullable = false, unique = true, length = 50)
    private String fluctuationCode;

    @Column(name = "FLUCTUATION_TYPE", nullable = false, length = 30)
    private String fluctuationType; // BIRTH, DEATH, IMMIGRATION, EMIGRATION, INTERNAL_MIGRATION

    @Column(name = "CITIZEN_ID")
    private Long citizenId;

    @Column(name = "FULL_NAME", length = 255)
    private String fullName;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "DATE_OF_DEATH")
    private LocalDate dateOfDeath;

    @Column(name = "AREA_CODE", nullable = false, length = 20)
    private String areaCode;

    @Column(name = "FLUCTUATION_DATE", nullable = false)
    private LocalDate fluctuationDate;

    @Column(name = "MONTH", nullable = false)
    private Integer month;

    @Column(name = "YEAR", nullable = false)
    private Integer year;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "DOCUMENT_NUMBER", length = 100)
    private String documentNumber;

    @Column(name = "DECLARED_BY", length = 200)
    private String declaredBy;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (fluctuationDate == null) {
            fluctuationDate = LocalDate.now();
        }
        if (month == null) {
            month = fluctuationDate.getMonthValue();
        }
        if (year == null) {
            year = fluctuationDate.getYear();
        }
    }
}
