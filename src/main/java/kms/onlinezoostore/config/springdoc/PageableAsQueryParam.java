package kms.onlinezoostore.config.springdoc;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameters(
    { @Parameter(
            in = ParameterIn.QUERY,
            description = "Page number (1..N)",
            name = "pageNumber",
            schema = @Schema(type = "integer", defaultValue = "1")
    ), @Parameter(
            in = ParameterIn.QUERY,
            description = "The size of the page to be returned",
            name = "pageSize",
            schema = @Schema(type = "integer", defaultValue = "15")
    ), @Parameter(
            in = ParameterIn.QUERY,
            description = "Sorting criteria in the format: property,(asc|desc). e.g sort=name,asc \n" +
                    "Default sort order is ascending. Multiple sort criteria are supported.",
            name = "sort",
            array = @ArraySchema(schema = @Schema(type = "string"))
    )})
public @interface PageableAsQueryParam {
}
