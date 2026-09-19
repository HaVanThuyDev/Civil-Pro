package vn.civilpro.statistical.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "POPULATION_STATISTIC", indexes = {
        @Index(name = "IDX_STAT_YEAR", columnList = "YEAR"),
        @Index(name = "IDX_STAT_AREA", columnList = "AREA_CODE")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UQ_STAT_AREA_YEAR_MONTH", columnNames = {"AREA_CODE", "YEAR", "MONTH"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopulationStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AREA_CODE", nullable = false, length = 20)
    private String areaCode;

    @Column(name = "AREA_NAME", length = 100)
    private String areaName;

    @Column(name = "YEAR", nullable = false)
    private Integer year;

    /** Null = annual, value = monthly */
    @Column(name = "MONTH")
    private Integer month;

    @Column(name = "TOTAL_POPULATION")
    @Builder.Default
    private Long totalPopulation = 0L;

    @Column(name = "TOTAL_MALE")
    @Builder.Default
    private Long totalMale = 0L;

    @Column(name = "TOTAL_FEMALE")
    @Builder.Default
    private Long totalFemale = 0L;

    @Column(name = "HOUSEHOLD_COUNT")
    @Builder.Default
    private Integer householdCount = 0;

    @Column(name = "POPULATION_0_14")
    @Builder.Default
    private Long population0To14 = 0L;

    @Column(name = "POPULATION_15_64")
    @Builder.Default
    private Long population15To64 = 0L;

    @Column(name = "POPULATION_65_PLUS")
    @Builder.Default
    private Long population65Plus = 0L;

    @Column(name = "POPULATION_DENSITY", precision = 10)
    private Double populationDensity;

    @Column(name = "CALCULATED_AT")
    private LocalDateTime calculatedAt;

    @PrePersist
    public void prePersist() {
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
    }
}
