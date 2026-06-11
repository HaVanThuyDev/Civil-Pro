package vn.civilpro.helper;

import org.springframework.data.jpa.domain.Specification;
import vn.civilpro.model.entity.SystemLog;

import java.time.LocalDateTime;

public class SystemLogSpec {

    public static Specification<SystemLog> hasLevel(String level) {
        return (root, query, cb) ->
                level == null ? null : cb.equal(root.get("logLevel"), level);
    }

    public static Specification<SystemLog> hasModule(String module) {
        return (root, query, cb) ->
                module == null ? null : cb.equal(root.get("module"), module);
    }

    public static Specification<SystemLog> fromDate(LocalDateTime from) {
        return (root, query, cb) ->
                from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<SystemLog> toDate(LocalDateTime to) {
        return (root, query, cb) ->
                to == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    // gộp tất cả filter vào 1 method
    public static Specification<SystemLog> filter(
            String level, String module,
            LocalDateTime from, LocalDateTime to) {
        return Specification
                .where(hasLevel(level))
                .and(hasModule(module))
                .and(fromDate(from))
                .and(toDate(to));
    }
}