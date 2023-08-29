package kms.onlinezoostore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "attached_files", uniqueConstraints = @UniqueConstraint(columnNames = {"file_path"}))
public class AttachedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // TODO: а нужно ли оно тут, и в таблице вообще

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_name", nullable = false)
    @Size(max = 100, message = "File name should be less then 100 characters.")
    private String fileName;

    @NotNull(message = "Owner ID should not be empty")
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotBlank(message = "Owner class should not be empty")
    @Column(name = "owner_class", nullable = false)
    private String ownerClass;


//    @ManyToOne
//    @JoinColumn(name="owner_entity_id", referencedColumnName="id")
//    @Where(clause = "owner_entity_class = 'Product'")
//    private Product ownerProduct;
//
//    @OneToOne
//    @JoinColumn(name="owner_entity_id", referencedColumnName="id")
//    @Where(clause = "owner_entity_class = 'ProductCategory'")
//    private ProductCategory ownerProductCategory;
//
//    @OneToOne
//    @JoinColumn(name="owner_entity_id", referencedColumnName="id")
//    @Where(clause = "owner_entity_class = 'Brand'")
//    private Brand ownerBrand;
}