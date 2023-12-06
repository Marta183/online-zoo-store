package kms.onlinezoostore.controllers.v1;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.dto.user.ChangePasswordRequestDto;
import kms.onlinezoostore.dto.user.UserCreateRequestDto;
import kms.onlinezoostore.dto.user.UserResponseDto;
import kms.onlinezoostore.dto.user.UserUpdateRequestDto;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = UserController.REST_URL)
public class UserController {

    static final String REST_URL = "/api/v1/users";

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping
    @JsonView(UserViews.Admin.class)
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponseDto> findAll(@RequestParam(required = false) UserRole role, Pageable pageable) {
        if (Objects.isNull(role)) {
            return userService.findPage(pageable);
        }
        return userService.findPageByRole(role, pageable);
    }

    @GetMapping("/profile")
    @JsonView(UserViews.Client.class)
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getProfile(Principal connectedUser) {
        return userService.getOwnProfile(connectedUser);
    }

    @PostMapping("/admin")
    @JsonView(UserViews.Admin.class)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createAdminUser(@RequestBody @Valid UserCreateRequestDto request) {
        return userService.createAdmin(request);
    }

    @PatchMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public void updateOwnProfile(@RequestBody @Valid UserUpdateRequestDto request, Principal connectedUser) {
        userService.updateOwnProfile(connectedUser, request);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@RequestBody @Valid ChangePasswordRequestDto request, Principal connectedUser) {
        userService.updatePassword(connectedUser, request);
    }

    @PatchMapping("/{email}/status")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserStatus(@PathVariable String email, @RequestParam @NotNull UserStatus status) {
        userService.updateUserStatus(email, status);
    }

    @PutMapping("/revoke-all")
    @ResponseStatus(HttpStatus.OK)
    public void revokeAllUsers() {
        tokenService.revokeAllTokens();
    }

//    @GetMapping("/admin/profile")
//    @PreAuthorize("hasAuthority(ADMIN.name())")
//    public String adminProfile() {
//        return null;
//    }
}
