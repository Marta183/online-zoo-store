package kms.onlinezoostore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product_categories", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "parent_id"}))
public class ProductCategory {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private ProductCategory parent;

//    @OneToOne(mappedBy = "ownerProductCategory", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private AttachedFile attachedImage;

//    @OneToOne(mappedBy = "ownerProductCategory", cascade = CascadeType.ALL)
////    @OneToOne(cascade = CascadeType.ALL)
////    @JoinColumn(name = "id", referencedColumnName = "owner_entity_id")
////    @Where(clause = "owner_entity_class = 'ProductCategory'")
//    private AttachedFile attachedImage;

//    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<ProductCategory> innerCategories;
//
//    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Product> products;

//    @Transient
//    private Double maxPrice;
}
