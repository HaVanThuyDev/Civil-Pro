package vn.civilpro.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.civilpro.model.entity.SystemLog;
import java.time.LocalDateTime;
@Getter
@Setter
@Data

public class SystemLogDto {
    private Long id;
    private String performedBy;
    private String actionType;
    private String module;
    private String description;
    private String targetId;
    private String targetType;
    private String ipAddress;
    private String logLevel = "INFO";
    private LocalDateTime createdAt = LocalDateTime.now();
    private String errorCode;
    private Object detailsJson;

    public void SystemLogDto (SystemLog systemLog){
        this.id=systemLog.getId();
        this.performedBy=systemLog.getPerformedBy();
        this.actionType=systemLog.getActionType();
        this.module=systemLog.getModule();
        this.description=systemLog.getDescription();
        this.targetId=systemLog.getTargetId();
        this.targetType=systemLog.getTargetType();
        this.ipAddress=systemLog.getIpAddress();
        this.logLevel=systemLog.getLogLevel();
        this.createdAt=systemLog.getCreatedAt();
        this.errorCode=systemLog.getErrorCode();
        this.detailsJson=systemLog.getDetailsJson();

    }
}
