package vn.civilpro.mapper;

import org.mapstruct.Mapper;
import vn.civilpro.model.dto.SystemLogDto;
import vn.civilpro.model.entity.SystemLog;

@Mapper(componentModel = "spring")
public interface SystemLogMapper extends BaseMapper<SystemLog, SystemLogDto> {
}