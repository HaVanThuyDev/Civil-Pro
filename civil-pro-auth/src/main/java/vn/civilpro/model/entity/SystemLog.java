package vn.civilpro.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "SYSTEM_LOGS",
        indexes = {
                @Index(name = "IDX_LOG_USER",       columnList = "PERFORMED_BY"),
                @Index(name = "IDX_LOG_CREATED_AT",  columnList = "CREATED_AT"),
                @Index(name = "IDX_LOG_ACTION",     columnList = "ACTION_TYPE"),
                @Index(name = "IDX_LOG_LEVEL",      columnList = "LOG_LEVEL"),
                @Index(name = "IDX_LOG_MODULE",     columnList = "MODULE")
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PERFORMED_BY", length = 100)
    private String performedBy;

    @Column(name = "ACTION_TYPE", nullable = false, length = 50)
    private String actionType;

    @Column(name = "MODULE", length = 100)
    private String module;

    @Column(name = "DESCRIPTION", nullable = false, length = 500)
    private String description;

    @Column(name = "TARGET_ID", length = 100)
    private String targetId;

    @Column(name = "TARGET_TYPE", length = 100)
    private String targetType;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;

    @Column(name = "LOG_LEVEL", nullable = false, length = 20)
    @Builder.Default
    private String logLevel = "INFO";

    @Column(name = "CREATED_AT", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "ERROR_CODE", length = 50)
    private String errorCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "DETAILS_JSON", columnDefinition = "JSON")
    private Object detailsJson;

    public static SystemLog info(String user, String module,
                                 String description, String ipAddress) {
        return SystemLog.builder()
                .performedBy(user)
                .actionType("USER")
                .module(module)
                .description(description)
                .ipAddress(ipAddress)
                .logLevel("INFO")
                .build();
    }

    public static SystemLog security(String user, String description,
                                     String ipAddress) {
        return SystemLog.builder()
                .performedBy(user)
                .actionType("SECURITY")
                .module("AUTH")
                .description(description)
                .ipAddress(ipAddress)
                .logLevel("WARNING")
                .build();
    }

    public static SystemLog systemError(String description, String errorCode) {
        return SystemLog.builder()
                .actionType("SYSTEM")
                .module("SYSTEM")
                .description(description)
                .errorCode(errorCode)
                .logLevel("ERROR")
                .build();
    }

    public void error(String failedToSaveSystemLog, Exception e) {
    }
}