package vn.civilpro.common.base;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class BaseSpec {

    private BaseSpec() {} // không cho khởi tạo

    public static <T> Specification<T> hasField(String field, String value) {
        return (root, query, cb) ->
                value == null || value.isBlank() ? null
                        : cb.equal(root.get(field), value);
    }

    public static <T> Specification<T> contains(String field, String keyword) {
        return (root, query, cb) ->
                keyword == null || keyword.isBlank() ? null
                        : cb.like(cb.lower(root.get(field)),
                        "%" + keyword.toLowerCase() + "%");
    }

    public static <T> Specification<T> fromDate(String field, LocalDateTime from) {
        return (root, query, cb) ->
                from == null ? null
                        : cb.greaterThanOrEqualTo(root.get(field), from);
    }

    public static <T> Specification<T> toDate(String field, LocalDateTime to) {
        return (root, query, cb) ->
                to == null ? null
                        : cb.lessThanOrEqualTo(root.get(field), to);
    }

    public static <T> Specification<T> isActive() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), "ACTIVE");
    }

    // parse date an toàn — dùng chung
    public static LocalDateTime parseDate(String date) {
        try {
            return date == null ? null : LocalDateTime.parse(date);
        } catch (Exception e) {
            return null;
        }
    }
}