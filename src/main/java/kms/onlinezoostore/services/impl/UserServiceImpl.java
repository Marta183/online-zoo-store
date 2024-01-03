package kms.onlinezoostore.services.impl;

import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.dto.user.ChangePasswordRequest;
import kms.onlinezoostore.dto.user.UserCreateRequest;
import kms.onlinezoostore.dto.user.UserResponse;
import kms.onlinezoostore.dto.user.UserUpdateRequest;
import kms.onlinezoostore.dto.mappers.UserCreateRequestMapper;
import kms.onlinezoostore.dto.mappers.UserResponseMapper;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.UserStatus;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.entities.enums.UserRole;
import kms.onlinezoostore.exceptions.authentication.InvalidVerificationLink;
import kms.onlinezoostore.repositories.UserRepository;
import kms.onlinezoostore.services.CartService;
import kms.onlinezoostore.services.UserService;
import kms.onlinezoostore.services.WishListService;
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
    private final WishListService wishListService;
    private final CartService cartService;

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
    public Page<UserResponse> findPage(Pageable pageable) {
        log.debug("Finding {} page ", ENTITY_CLASS_NAME);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponse> page = userRepository.findAll(pageable).map(userResponseMapper::mapToDto);

        log.debug("Found {} page with number of elements: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<UserResponse> findPageByRole(UserRole role, Pageable pageable) {
        log.debug("Finding {} page ", ENTITY_CLASS_NAME);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponse> page = userRepository.findAllByRole(role, pageable).map(userResponseMapper::mapToDto);

        log.debug("Found {} page with number of elements: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public UserResponse getOwnProfile(Principal connectedUser) {
        log.debug("Get {} own profile by e-mail {}", ENTITY_CLASS_NAME, connectedUser.getName());

        User user = UsersUtil.extractUser(connectedUser);
        return userResponseMapper.mapToDto(user);
    }

    @Override
    @Transactional
    public void updateOwnProfile(Principal connectedUser, UserUpdateRequest request) {
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
    public void updatePassword(Principal connectedUser, ChangePasswordRequest request) {
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
    public UserResponse createAdmin(UserCreateRequest request) {
        log.info("Creating new admin {} with e-mail {}", ENTITY_CLASS_NAME, request.getEmail());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(userRepository, "email", request.getEmail());

        User user = userRequestMapper.mapToEntity(request);
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ADMIN);
        user.setDefaultStatus();
        User savedUser = userRepository.save(user);
        user = null; request = null;

        UserResponse userDto = userResponseMapper.mapToDto(savedUser);

        log.info("New admin {} with e-mail {} created in DB", ENTITY_CLASS_NAME, savedUser.getEmail());
        return userDto;
    }

    @Override
    @Transactional
    public User createClient(UserCreateRequest request) {
        log.info("Creating new client {} with e-mail {}", ENTITY_CLASS_NAME, request.getEmail());

        User existingUser = userRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);
        if (existingUser != null) {
            throw (existingUser.isEnabled()) ?
                new EntityDuplicateException("This account has already been registered, please, login.") :
                new InvalidVerificationLink("This account has already been registered, please, verify your email to finish registration process");
        }

        User user = userRequestMapper.mapToEntity(request);
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CLIENT);
        user.setDefaultStatus();
        User savedUser = userRepository.save(user);
        user = null; request = null;

        log.info("New client {} with e-mail {} created in DB", ENTITY_CLASS_NAME, savedUser.getEmail());
        return savedUser;
    }

    @Override
    @Transactional
    public void createRelatedEntities(User user) {
        cartService.createCartForUser(user);
        wishListService.createDefaultWishListForUser(user);
    }
}
