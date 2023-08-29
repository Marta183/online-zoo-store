package kms.onlinezoostore.services.files.images;

import java.util.Arrays;

public enum AllowedImageExtensions {
    PNG,
    JPG,
    JPEG,
    BMP;

    public static String valuesAsString() {
        return String.join(", ", Arrays.stream(values()).map(AllowedImageExtensions::name).toArray(String[]::new));
    }
}
