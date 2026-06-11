package vn.civilpro.spec;

import org.springframework.data.jpa.domain.Specification;
import vn.civilpro.common.base.BaseSpec;
import vn.civilpro.model.entity.User;
import java.util.Map;

public class UserSpec {

    private UserSpec() {}

    public static Specification<User> filter(Map<String, String> f) {
        if (f == null || f.isEmpty()) return Specification.where(null);

        return Specification
                .where(BaseSpec.<User>hasField("status",   f.get("status")))
                .and(BaseSpec.hasField("role",             f.get("role")))
                .and(BaseSpec.contains("username",         f.get("keyword")))
                .and(BaseSpec.fromDate("createdAt",
                        BaseSpec.parseDate(f.get("from"))))  // dùng parseDate từ BaseSpec
                .and(BaseSpec.toDate("createdAt",
                        BaseSpec.parseDate(f.get("to"))));
    }
}