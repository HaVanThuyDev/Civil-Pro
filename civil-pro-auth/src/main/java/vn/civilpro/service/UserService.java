package vn.civilpro.service;

import org.springframework.data.domain.Page;
import vn.civilpro.model.dto.request.ChangePasswordRequest;
import vn.civilpro.model.dto.request.CreateUserRequest;
import vn.civilpro.model.dto.request.UpdateUserRequest;
import vn.civilpro.model.entity.User;
import vn.civilpro.model.projection.UserView;

import java.util.Map;

public interface UserService {
    Page<UserView> search(Map<String, String> filters, int page, int size);
    User getById(Long id);
    User getByUsername(String username);
    User create(CreateUserRequest request);
    User update(Long id, UpdateUserRequest request);
    void changePassword(Long id, ChangePasswordRequest request);
    void delete(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}