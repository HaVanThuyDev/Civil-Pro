package vn.civilpro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.civilpro.model.entity.SystemLog;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}
