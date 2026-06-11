package vn.civilpro.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import vn.civilpro.common.base.BaseRepository;
import vn.civilpro.model.entity.User;
import vn.civilpro.model.projection.UserView;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    <T> Page<T> findBy(Specification<User> spec, org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<T> query);
}