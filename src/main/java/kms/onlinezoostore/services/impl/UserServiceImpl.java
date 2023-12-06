package kms.onlinezoostore.services.impl;

import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.dto.user.ChangePasswordRequestDto;
import kms.onlinezoostore.dto.user.UserCreateRequestDto;
import kms.onlinezoostore.dto.user.UserResponseDto;
import kms.onlinezoostore.dto.user.UserUpdateRequestDto;
import kms.onlinezoostore.dto.mappers.UserCreateRequestMapper;
import kms.onlinezoostore.dto.mappers.UserResponseMapper;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.UserStatus;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.entities.enums.UserRole;
import kms.onlinezoostore.repositories.UserRepository;
import kms.onlinezoostore.services.UserService;
import kms.onlinezoostore.utils.UniqueFieldService;
import kms.onlinezoostore.utils.UsersUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCreateRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;
    private final UniqueFieldService uniqueFieldService;
    private final PasswordEncoder passwordEncoder;

    private static final String ENTITY_CLASS_NAME = "USER";

    @Override
    public User findByEmail(String email) {
        log.debug("Finding {} by e-mail {}", ENTITY_CLASS_NAME, email);

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, email));

        log.debug("Found {} by e-mail {}", ENTITY_CLASS_NAME, email);
        return user;
    }

    @Override
    public Page<UserResponseDto> findPage(Pageable pageable) {
        log.debug("Finding {} page ", ENTITY_CLASS_NAME);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponseDto> page = userRepository.findAll(pageable).map(userResponseMapper::mapToDto);

        log.debug("Found {} page with number of elements: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<UserResponseDto> findPageByRole(UserRole role, Pageable pageable) {
        log.debug("Finding {} page ", ENTITY_CLASS_NAME);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponseDto> page = userRepository.findAllByRole(role, pageable).map(userResponseMapper::mapToDto);

        log.debug("Found {} page with number of elements: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public UserResponseDto getOwnProfile(Principal connectedUser) {
        log.debug("Get {} own profile by e-mail {}", ENTITY_CLASS_NAME, connectedUser.getName());

        User user = UsersUtil.extractUser(connectedUser);
        return userResponseMapper.mapToDto(user);
    }

    @Override
    @Transactional
    public void updateOwnProfile(Principal connectedUser, UserUpdateRequestDto request) {
        log.debug("Updating {} own profile by e-mail {}", ENTITY_CLASS_NAME, connectedUser.getName());

        User user = UsersUtil.extractUser(connectedUser);

        if (!StringUtils.isBlank(request.getFirstName()))
            user.setFirstName(request.getFirstName());
        if (!StringUtils.isBlank(request.getLastName()))
            user.setLastName(request.getLastName());

        userRepository.save(user);
        log.debug("Updated {} own profile by e-mail {}", ENTITY_CLASS_NAME, connectedUser.getName());
    }

    @Override
    @Transactional
    public void updatePassword(Principal connectedUser, ChangePasswordRequestDto request) {
        log.debug("Updating {} password by email {}", ENTITY_CLASS_NAME, connectedUser.getName());

        User user = UsersUtil.extractUser(connectedUser);

        if (!user.getEmail().equals(request.getEmail())) {
            throw new BadCredentialsException("Token doesn't belong to email " + request.getEmail());
        }
        // check current password
        if (!oldPasswordIsValid(user, request.getCurrentPassword())) {
            log.info("Changing password for user with email {}: old password is not valid!", user.getEmail());
            throw new BadCredentialsException("Invalid current password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        request = null;

        log.debug("Updated password for {} with email {}", ENTITY_CLASS_NAME, connectedUser.getName());
    }

    private boolean oldPasswordIsValid(User user, @NotNull String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    @Transactional
    public void updateUserStatus(String email, UserStatus newStatus) {
        log.debug("Updating {} status by email {}", ENTITY_CLASS_NAME, email);

        User existingUser = userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, email));
        existingUser.setStatus(newStatus);

        log.debug("Updated status for {} with email {}", ENTITY_CLASS_NAME, email);
    }

    @Override
    @Transactional
    public UserResponseDto createAdmin(UserCreateRequestDto request) {
        log.info("Creating new admin {} with e-mail {}", ENTITY_CLASS_NAME, request.getEmail());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(userRepository, "email", request.getEmail());

        User user = userRequestMapper.mapToEntity(request);
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ADMIN);
        user.setDefaultStatus();
        User savedUser = userRepository.save(user);
        user = null; request = null;

        UserResponseDto userDto = userResponseMapper.mapToDto(savedUser);

        log.info("New admin {} with e-mail {} created in DB", ENTITY_CLASS_NAME, savedUser.getEmail());
        return userDto;
    }
}
