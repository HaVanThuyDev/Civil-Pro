package vn.civilpro.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.model.dto.request.*;
import vn.civilpro.model.entity.Role;
import vn.civilpro.model.entity.User;
import vn.civilpro.model.projection.UserView;
import vn.civilpro.repository.RoleRepository;
import vn.civilpro.repository.UserRepository;
import vn.civilpro.service.UserService;
import vn.civilpro.spec.UserSpec;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository  userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserView> search(Map<String, String> filters, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0), size <= 0 ? 20 : size,
                Sort.by("createdAt").descending());
        return userRepository.findBy(
                UserSpec.filter(filters),
                q -> q.as(UserView.class).page(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    @Transactional
    public User create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username đã tồn tại: " + request.getUsername());
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email đã tồn tại: " + request.getEmail());

        // load roles từ DB
        Set<Role> roles = resolveRoles(request.getRoleIds());

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // passwordHash
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .administrativeUnitCode(request.getAdministrativeUnitCode())
                .status(1)  // Integer, mặc định ACTIVE
                .roles(roles)
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, UpdateUserRequest request) {
        User user = getById(id);

        if (request.getFullName()              != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber()           != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAdministrativeUnitCode()!= null) user.setAdministrativeUnitCode(request.getAdministrativeUnitCode());
        if (request.getAvatarUrl()             != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getStatus()                != null) user.setStatus(request.getStatus());

        if (request.getEmail() != null
                && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail()))
                throw new RuntimeException("Email đã tồn tại: " + request.getEmail());
            user.setEmail(request.getEmail());
        }

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            user.setRoles(resolveRoles(request.getRoleIds()));
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new RuntimeException("Mật khẩu xác nhận không khớp");

        User user = getById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash()))
            throw new RuntimeException("Mật khẩu cũ không đúng");

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.delete(getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // load roles từ DB — tránh set role không tồn tại
    private Set<Role> resolveRoles(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }
}