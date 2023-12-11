package kms.onlinezoostore.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "OpenApi specification for PawSome - online store (Team Challenge)",
                version = "1.0"
        ),
        servers = {
                @Server(description = "PROD ENVIRONMENT", url = "https://online-zoo-store-backend-web-service.onrender.com"),
                @Server(description = "LOCAL ENVIRONMENT", url = "http://localhost:8088/online_zoo_store")
        },
        security = {
                @SecurityRequirement(name = "BearerAuthentication")
        }
)
@SecurityScheme(
        name = "BearerAuthentication",
        description = "JWT authentication. Connect to get your JWT token",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
