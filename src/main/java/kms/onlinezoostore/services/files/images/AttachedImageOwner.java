package kms.onlinezoostore.services.files.images;

public interface AttachedImageOwner {
    Long getId();
    String getImageOwnerClassName();

    default String toStringImageOwner() {
        return getImageOwnerClassName() + " ID = " + getId();
    }
}
