package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.user.ChangePasswordRequestDto;
import kms.onlinezoostore.dto.user.UserCreateRequestDto;
import kms.onlinezoostore.dto.user.UserResponseDto;
import kms.onlinezoostore.dto.user.UserUpdateRequestDto;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.UserRole;
import kms.onlinezoostore.entities.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface UserService {

    User findByEmail(String email);

    Page<UserResponseDto> findPage(Pageable pageable);
    Page<UserResponseDto> findPageByRole(UserRole role, Pageable pageable);

    UserResponseDto getOwnProfile(Principal connectedUser);

    void updateOwnProfile(Principal connectedUser, UserUpdateRequestDto request);

    void updateUserStatus(String email, UserStatus newStatus);

    void updatePassword(Principal connectedUser, ChangePasswordRequestDto request);

    UserResponseDto createAdmin(UserCreateRequestDto request);
}
