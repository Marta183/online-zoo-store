package kms.onlinezoostore.controllers.v1;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.config.springdoc.PageableAsQueryParam;
import kms.onlinezoostore.dto.user.ChangePasswordRequest;
import kms.onlinezoostore.dto.user.UserCreateRequest;
import kms.onlinezoostore.dto.user.UserResponse;
import kms.onlinezoostore.dto.user.UserUpdateRequest;
import kms.onlinezoostore.dto.view.UserViews;
import kms.onlinezoostore.entities.enums.UserRole;
import kms.onlinezoostore.entities.enums.UserStatus;
import kms.onlinezoostore.services.TokenService;
import kms.onlinezoostore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Objects;

@RestController
@Tag(name = "Users")
@RequiredArgsConstructor
@RequestMapping(value = UserController.REST_URL)
public class UserController {

    static final String REST_URL = "/api/v1/users";

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping
    @JsonView(UserViews.Admin.class)
    @ResponseStatus(HttpStatus.OK)
    @PageableAsQueryParam
    @Operation(summary = "Get all users", description = "Retrieve a paginated list of users based on the specified role")
    public Page<UserResponse> findAll(@RequestParam(required = false) UserRole role, Pageable pageable) {
        if (Objects.isNull(role)) {
            return userService.findPage(pageable);
        }
        return userService.findPageByRole(role, pageable);
    }

    @GetMapping("/profile")
    @JsonView(UserViews.Client.class)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get own profile", description = "Retrieve the profile of the authenticated user")
    public UserResponse getProfile(@NotNull Principal connectedUser) {
        return userService.getOwnProfile(connectedUser);
    }

    @PostMapping("/admin")
    @JsonView(UserViews.Admin.class)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new admin user", description = "Create a new admin user with the provided details")
    public UserResponse createAdminUser(@RequestBody @Valid UserCreateRequest request) {
        return userService.createAdmin(request);
    }

    @PatchMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update own profile", description = "Update authenticated user profile")
    public void updateOwnProfile(@RequestBody @Valid UserUpdateRequest request,
                                 @NotNull Principal connectedUser) {
        userService.updateOwnProfile(connectedUser, request);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user password", description = "Update authenticated user password")
    public void updatePassword(@RequestBody @Valid ChangePasswordRequest request,
                               @NotNull Principal connectedUser) {
        userService.updatePassword(connectedUser, request);
    }

    @PatchMapping("/{email}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user status")
    public void updateUserStatus(@PathVariable String email,
                                 @RequestParam @NotNull UserStatus status) {
        userService.updateUserStatus(email, status);
    }

    @PutMapping("/revoke-all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Revoke all users",
               description = "Revoke all active user sessions by invalidating their authentication tokens")
    public void revokeAllUsers() {
        tokenService.revokeAllTokens();
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Remove user from the system (only for testing)")
    public void deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
    }

//    @GetMapping("/admin/profile")
//    @PreAuthorize("hasAuthority(ADMIN.name())")
//    public String adminProfile() {
//        return null;
//    }
}
