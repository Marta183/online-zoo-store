package kms.onlinezoostore.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import kms.onlinezoostore.dto.view.UserViews;
import kms.onlinezoostore.entities.enums.UserRole;
import kms.onlinezoostore.entities.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@Schema(name = "User")
public class UserResponse {

    @JsonView(UserViews.Admin.class)
    private final Long id;

    private final String firstName;
    private final String lastName;
    private final String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birthDate;

    @JsonView(UserViews.Admin.class)
    private final UserStatus status;

    @JsonView(UserViews.Admin.class)
    private final UserRole role;

    @JsonView(UserViews.Admin.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private final LocalDateTime createdAt;

    @JsonView(UserViews.Admin.class)
    private final boolean consentToProcessData;
}
