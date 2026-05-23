package vn.civilpro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.civilpro.model.entity.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long > {
    Optional<Role> findByRoleCode(String roleCode);

}
