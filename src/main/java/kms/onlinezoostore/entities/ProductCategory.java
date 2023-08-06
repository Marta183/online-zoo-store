package kms.onlinezoostore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
