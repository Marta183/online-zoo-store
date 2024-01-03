package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.user.ChangePasswordRequest;
import kms.onlinezoostore.dto.user.UserCreateRequest;
import kms.onlinezoostore.dto.user.UserResponse;
import kms.onlinezoostore.dto.user.UserUpdateRequest;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.UserRole;
import kms.onlinezoostore.entities.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface UserService {

    User findByEmail(String email);

    Page<UserResponse> findPage(Pageable pageable);

    Page<UserResponse> findPageByRole(UserRole role, Pageable pageable);

    UserResponse getOwnProfile(Principal connectedUser);

    void updateOwnProfile(Principal connectedUser, UserUpdateRequest request);

    void updateUserStatus(String email, UserStatus newStatus);

    void updatePassword(Principal connectedUser, ChangePasswordRequest request);
    UserResponse createAdmin(UserCreateRequest request);

    User createClient(UserCreateRequest request);

    void createRelatedEntities(User user);
}
