package vn.civilpro.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.civilpro.mapper.SystemLogMapper;
import vn.civilpro.model.dto.SystemLogDto;
import vn.civilpro.repository.SystemLogRepository;
import vn.civilpro.service.SystemLogService;
import java.util.List;
@AllArgsConstructor
@Service

public class SystemLogServiceImpl implements SystemLogService {
    private final SystemLogRepository systemLogRepository;
    private final SystemLogMapper systemLogMapper;

    @Override
    public List<SystemLogDto> getSystemLogs() {
        return systemLogMapper.toDtoList(systemLogRepository.findAll());
    }
}
